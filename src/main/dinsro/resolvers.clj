(ns dinsro.resolvers
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.session :as mu.session]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as log]))

(defresolver current-user-resolver
  [_env _props]
  {::pc/output
   [{:session/current-user
     [:user/username
      {:user/ref [::m.users/id]}
      :user/valid?]}]}
  {:session/current-user
   {:user/username "admin"
    :user/ref {::m.users/id (q.users/find-eid-by-name "admin")}
    :user/valid?   true}})

(defresolver current-user-ref-resolver
  [_env _props]
  {::pc/output
   [{:session/current-user-ref [::m.users/id]}]}
  (when-let [user-id (q.users/find-eid-by-name "admin")]
    {:session/current-user-ref {::m.users/id user-id}}))

(def resolvers
  [current-user-resolver
   current-user-ref-resolver
   mu.session/resolvers])
