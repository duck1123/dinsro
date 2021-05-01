(ns dinsro.resolvers.currencies
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.currencies :as q.currencies]
   [taoensso.timbre :as timbre]))

(def sats {::m.currencies/id   "sats"
           ::m.currencies/name "Sats"})

(defresolver currencies-resolver
  [_env _props]
  {::pc/output [{:all-currencies [::m.currencies/id]}]}
  {:all-currencies
   (map (fn [{::m.currencies/keys [id]}]
          [::m.currencies/id id])
        (q.currencies/index-records))})

(defresolver currency-resolver
  [_env {::m.currencies/keys [id]}]
  {::pc/input  #{::m.currencies/id}
   ::pc/output [::m.currencies/name]}
  (let [eid (q.currencies/find-eid-by-id id)
        record (q.currencies/read-record eid)]
    (assoc record ::m.currencies/id id)))

(defresolver account-currencies-resolver
  [_env _props]
  {::pc/input  #{::m.accounts/id}
   ::pc/output [{::m.accounts/currencies [::m.currencies/id]}]}
  (q.currencies/index-records))

(defresolver user-currencies-resolver
  [_env {::m.users/keys [username]}]
  {::pc/input  #{::m.users/username}
   ::pc/output [{::m.users/currencies [::m.currencies/id]}]}
  (q.currencies/index-by-user username))

(def resolvers
  [account-currencies-resolver
   currencies-resolver
   currency-resolver
   user-currencies-resolver])
