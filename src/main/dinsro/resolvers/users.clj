(ns dinsro.resolvers.users
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation defresolver]]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defmutation delete!
  [_request {::m.users/keys [username]}]
  {::pc/params #{::m.users/username}
   ::pc/output [:status]}
  (q.users/delete-record username)
  {:status :success})

(defresolver user-resolver
  [_env {::m.users/keys [username]}]
  {::pc/input  #{::m.users/username}
   ::pc/output [::m.users/username]}
  (timbre/infof "resolving user: %s" username)
  (q.users/read-record username))

(defresolver user-link-resolver
  [_env {::m.users/keys [username]}]
  {::pc/input  #{::m.users/username}
   ::pc/output [{::m.users/link [::m.users/username]}]}
  {::m.users/link [[::m.users/username username]]})

(defresolver users-resolver
  [_env _props]
  {::pc/output [{:all-users [::m.users/username]}]}
  {:all-users
   (map (fn [{::m.users/keys [username]}]
          [::m.users/username username])
        (q.users/index-records))})

(def resolvers
  [user-resolver
   user-link-resolver
   users-resolver])

(comment
  (q.users/index-ids)
  (q.users/read-record 21))
