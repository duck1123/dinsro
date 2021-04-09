(ns dinsro.mutations.session
  (:require
   [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]]
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defmutation register
  [_env _params]
  {::pc/params #{:user/email :user/password}
   ::pc/output [:user/id :user/valid? :user/registered?]}

  (let [params {:user/id          "id"
                :user/valid?      true
                :user/registered? true}]
    (q.users/create-record params)))

(defmutation login
  [{{:keys [session]} :request} {:user/keys [email password]}]
  {::pc/params #{:user/email :user/password}
   ::pc/output [:user/id :user/valid?]}
  (if (= password "hunter2")
    (augment-response
     {:user/id     email
      :user/valid? true}
     (fn [ring-response]
       (assoc ring-response :session (assoc session :identity email))))
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
