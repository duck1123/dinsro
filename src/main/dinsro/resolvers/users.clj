(ns dinsro.resolvers.users
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as log]))

(>defn resolve-link
  [username]
  [::m.users/id => (s/keys)]
  {::m.users/link [(m.users/ident username)]})

(>defn resolve-user
  [username]
  [::m.users/id => ::m.users/item]
  (log/infof "resolving user: %s" username)
  (q.users/read-record username))

(>defn resolve-users
  []
  [=> (s/keys)]
  (let [records (q.users/index-records)]
    {:all-users
     (map (fn [{::m.users/keys [id]}]
            (m.users/ident id))
          records)}))

(defresolver user-resolver
  [_env {::m.users/keys [id]}]
  {::pc/input  #{::m.users/id}
   ::pc/output [::m.users/id]}
  (resolve-user id))

(defresolver user-link-resolver
  [_env {::m.users/keys [id]}]
  {::pc/input  #{::m.users/id}
   ::pc/output [{::m.users/link [::m.users/id]}]}
  (resolve-link id))

(defresolver users-resolver
  [_env _props]
  {::pc/output [{:all-users [::m.users/id]}]}
  (resolve-users))

(def resolvers
  [user-resolver
   user-link-resolver
   users-resolver])

(comment
  (q.users/index-ids)
  (q.users/read-record 21))
