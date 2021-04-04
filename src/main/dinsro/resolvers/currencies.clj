(ns dinsro.resolvers.currencies
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.actions.currencies :as a.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.currencies :as q.currencies]
   [taoensso.timbre :as timbre]))

(defresolver currencies-resolver
  [_env _props]
  {::pc/output [{:all-currencies [::m.currencies/id]}]}
  {:all-currencies (map (fn [id] [::m.currencies/id id]) (q.currencies/index-ids))})

(defresolver currency-resolver
  [_env {::m.currencies/keys [id]}]
  {::pc/input  #{::m.currencies/id}
   ::pc/output [::m.currencies/name]}
  (let [record (q.currencies/read-record id)]
    (assoc record ::m.currencies/id id)))

(defresolver user-currencies-resolver
  [_env _props]
  {::pc/input  #{::m.users/id}
   ::pc/output [{::m.users/currencies [::m.currencies/id]}]}
  (a.currencies/index-by-user-handler {}))

(def resolvers
  [currencies-resolver
   currency-resolver
   user-currencies-resolver])
