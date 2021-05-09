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
     (action
      [{:keys [_app state]}]
      (let [logged-in? (get-in @state [:session/current-user :user/valid?])]
        (when-not logged-in?
          (routing/route-to! "/login"))
        (swap! state #(assoc % :root/ready? true))))))

#?(:clj
   (>defn do-register
     [id password]
     [::m.users/id ::m.users/password => (s/keys)]
     (let [params #::m.users{:password password :id id}]
       (try
         (a.authentication/register (log/spy :info params))
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

#?(:clj
   (pc/defmutation login
     [{{:keys [session]} :request} {:user/keys [username password]}]
     {::pc/params #{:user/username :user/password}
      ::pc/output [:user/username :user/valid?]}
     (if-let [_user (q.users/find-by-id username)]
       (if (= password "hunter2")
         (augment-response
          {:user/username username
           :user/valid?   true}
          (fn [ring-response]
            (assoc ring-response :session (assoc session :identity username))))
         {:user/username nil
          :user/valid?   false})
       {:user/username nil
        :user/valid?   false}))
   :cljs
   (fm/defmutation login [_]
     (action
      [{:keys [state]}]
      (log/info "busy"))

     (error-action
      [{:keys [state]}]
      (log/info "error action"))

     (ok-action
      [{:keys [state] :as env}]
      (log/infof "ok")
      (let [{:user/keys [valid?]} (get-in env [:result :body `login])]
        (when-not valid?
          (swap! state #(assoc-in % [:component/id :dinsro.ui.forms.login/form :user/message]
                                  "Can't log in")))))

     (remote
      [env]
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
     (action
      [{:keys [state]}]
      (log/info "busy"))

     (error-action
      [{:keys [state]}]
      (log/info "error action"))

     (ok-action
      [{:keys [state] :as env}]
      (log/infof "ok"))

     (remote
      [env]
      (fm/with-target env [:session/current-user]))))

#?(:clj
   (def resolvers [login logout register]))
