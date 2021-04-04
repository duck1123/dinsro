(ns dinsro.resolvers.users
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defresolver user-resolver
  [_env {::m.users/keys [id]}]
  {::pc/input  #{::m.users/id}
   ::pc/output [::m.users/email
                ::m.users/name]}
  (timbre/infof "resolving user: %s" id)
  (q.users/read-record id))

(defresolver users-resolver
  [_env _props]
  {::pc/output [{:all-users [::m.users/id]}]}
  {:all-users
   (map (fn [id] [::m.users/id id]) (q.users/index-ids))})

(def resolvers
  [user-resolver
   users-resolver])

(comment
  (q.users/index-ids)
  (q.users/read-record 21))
