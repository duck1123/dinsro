(ns dinsro.resolvers.users
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.users :as m.users]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))

(defresolver user-resolver
  [_env {::m.users/keys [id]}]
  {::pc/input #{::m.users/id}
   ::pc/output [::m.users/email
                ::m.users/name]}
  (timbre/infof "resolving user: %s" id)
  (get sample/user-map id))

(defresolver users-resolver
  [_env _props]
  {::pc/output [{:all-users [::m.users/id]}]}
  {:all-users
   (map (fn [id] [::m.users/id id]) (keys sample/user-map))})

(defresolver user-map-resolver
  [_env _props]
  {::pc/output [::m.users/map]}
  {::m.users/map sample/user-map})

(def resolvers
  [user-resolver
   users-resolver
   user-map-resolver])
