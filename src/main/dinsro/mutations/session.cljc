(ns dinsro.mutations.session
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   #?(:clj [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]])
   #?(:clj [com.fulcrologic.guardrails.core :refer [>defn =>]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.users :as q.users])
   #?(:cljs [dinsro.routing :as routing])
   [taoensso.timbre :as log]))

(comment ::m.users/_ ::pc/_ ::s/_)

(defsc CurrentUser
  [_this _props]
  {:query [:user/username :user/valid?]})

#?(:cljs
   (fm/defmutation finish-login [_]
     (action [{:keys [_app state]}]
       (let [logged-in? (get-in @state [:session/current-user :user/valid?])]
         (when-not logged-in?
           (routing/route-to! "/login"))
         (swap! state #(assoc % :root/ready? true))))))

#?(:clj
   (>defn do-register
     [name password]
     [::m.users/name ::m.users/password => (s/keys)]
     (let [params #::m.users{:password password :name name}]
       (try
         (a.authentication/register params)
         (catch Exception ex
           ;; (log/error ex "error")
           {::error true
            :ex     (str ex)})))))

#?(:clj
   (pc/defmutation register
     [_env {:user/keys [password username]}]
     {::pc/params #{:user/password :user/username}
      ::pc/output [:user/username :user/valid? :user/registered?]}
     (log/info "register")
     (do-register username password))
   :cljs
   (fm/defmutation register [_]
     (action [_env] (log/info "register"))
     (remote [_env] true)))

(s/def ::login-response (s/keys))

#?(:clj
   (>defn do-login
     [session username password]
     [any? ::m.users/name ::m.users/password => ::login-response]
     (if-let [user-id (q.users/find-eid-by-name username)]
       (if (= password "hunter2")
         (let [response {:user/username username
                         :session/current-user-ref {::m.users/id user-id}
                         :user/valid?   true}
               handler  (fn [ring-response]
                          (assoc ring-response :session (assoc session :identity username)))]
           (augment-response response handler))
         {:user/username nil
          :user/valid?   false})
       {:user/username nil
        :user/valid?   false})))

#?(:clj
   (pc/defmutation login
     [env {:user/keys [username password]}]
     {::pc/params #{:user/username :user/password}
      ::pc/output [:user/username
                   :user/valid?
                   {:session/current-user-ref [::m.users/id]}]}
     (let [{:keys [request]} env
           {:keys [session]} request]
       (do-login session username password)))
   :cljs
   (fm/defmutation login [_]
     (action [{:keys [state]}]
       (log/info "busy"))

     (error-action [{:keys [state]}]
       (log/info "error action"))

     (ok-action [{:keys [state] :as env}]
       (let [body (get-in env [:result :body])
             {:user/keys [valid?]
              :session/keys [current-user-ref]} (get body `login)]
         (when-not valid?
           (-> state
               (swap! #(assoc-in % [:component/id :dinsro.ui.forms.login/form :user/message]
                                 "Can't log in"))
               (swap! #(assoc-in % [:component/id :dinsro.ui.navbar/Navbar :session/current-user-ref]
                                 current-user-ref))))))

     (remote [env]
       (-> env
           (fm/returning CurrentUser)
           (fm/with-target [:session/current-user])))))

#?(:clj
   (pc/defmutation logout
     [{{:keys [session]} :request} _]
     {::pc/params #{}
      ::pc/output [:user/username :user/valid?]}
     (augment-response
      {:user/username nil
       :user/valid?   false}
      (fn [ring-response]
        (assoc ring-response :session (assoc session :identity nil)))))
   :cljs
   (fm/defmutation logout [_]
     (action [{:keys [state]}]
       (swap! state #(assoc-in % [:component/id :dinsro.ui.navbar/Navbar :session/current-user-ref]
                               nil)))

     (error-action [{:keys [state]}]
       (log/info "error action"))

     (ok-action [{:keys [state] :as env}]
       (log/infof "ok"))

     (remote [env]
       (fm/with-target env [:session/current-user]))))

#?(:clj
   (def resolvers [login logout register]))
