(ns dinsro.mutations.session
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.actions.authentication :as a.authentication]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as log]))

(>defn do-register
  [id password]
  [::m.users/id ::m.users/password => (s/keys)]
  (let [params #::m.users{:password password :id id}]
    (try
      (a.authentication/register (log/spy :info params))
      (catch Exception ex
        ;; (log/error ex "error")
        {::error true
         :ex     (str ex)}))))

(defmutation register
  [_env {:user/keys [password username]}]
  {::pc/params #{:user/password :user/username}
   ::pc/output [:user/username :user/valid? :user/registered?]}
  (log/info "register")
  (do-register username password))

(defmutation login
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

(defmutation logout
  [{{:keys [session]} :request} _]
  {::pc/params #{}
   ::pc/output [:user/username :user/valid?]}
  (augment-response
   {:user/username nil
    :user/valid?   false}
   (fn [ring-response]
     (assoc ring-response :session (assoc session :identity nil)))))

(def resolvers [login logout register])
