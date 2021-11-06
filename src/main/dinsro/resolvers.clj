(ns dinsro.resolvers
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as log]))

(defattr current-user :auth/user :ref
  {ao/target    ::m.users/id
   ao/pc-output [{:session/current-user [::m.users/id]}]
   ao/pc-resolve (fn [_ _]
                   (let [username "admin"]
                     (when-let [user-id (q.users/find-eid-by-name username)]
                       {:session/current-user-ref {::m.users/id user-id}})))})

(defresolver current-user-resolver
  [_env props]
  {::pc/output
   [{:session/current-user
     [:user/username
      {:user/ref [::m.users/id]}
      :user/valid?]}]}
  (log/spy :info props)
  (let [username m.users/default-username
        user-id  (q.users/find-eid-by-name username)]
    {:session/current-user
     {:user/username username
      :user/ref      {::m.users/id user-id}
      :user/valid?   true}}))

(defresolver current-user-ref-resolver
  [_env _props]
  {::pc/output
   [{:session/current-user-ref [::m.users/id]}]}
  (let [username m.users/default-username]
    (when-let [user-id (q.users/find-eid-by-name username)]
      {:session/current-user-ref {::m.users/id user-id}})))

(def attributes [current-user])

(def resolvers
  [current-user-resolver
   current-user-ref-resolver])
