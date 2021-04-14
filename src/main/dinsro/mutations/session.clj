(ns dinsro.mutations.session
  (:require
   [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]]
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.actions.authentication :as a.authentication]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defmutation register
  [_env {::m.users/keys [email name password]}]
  {::pc/params #{:user/email :user/name :user/password}
   ::pc/output [:user/id :user/valid? :user/registered?]}
  (let [params #::m.users{:email    email
                          :password password
                          :name     name}]
    (try
      (a.authentication/register params)
      (catch Exception ex
        (timbre/error ex "error")
        {::error true
         :ex     ex}))))

(defmutation login
  [{{:keys [session]} :request} {:user/keys [email password]}]
  {::pc/params #{:user/email :user/password}
   ::pc/output [:user/id :user/valid?]}
  (if-let [_user (q.users/find-by-email email)]
    (if (= password "hunter2")
      (augment-response
       {:user/id     email
        :user/valid? true}
       (fn [ring-response]
         (assoc ring-response :session (assoc session :identity email))))
      {:user/id     nil
       :user/valid? false})
    {:user/id     nil
     :user/valid? false}))

(defmutation logout
  [{{:keys [session]} :request} _]
  {::pc/params #{}
   ::pc/output [:user/id :user/valid?]}
  (augment-response
   {:user/id     nil
    :user/valid? false}
   (fn [ring-response]
     (assoc ring-response :session (assoc session :identity nil)))))

(def resolvers [login logout register])
