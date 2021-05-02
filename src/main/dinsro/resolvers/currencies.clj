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
  {::pc/output [{:all-currencies [::m.currencies/name]}]}
  {:all-currencies
   (map (fn [{::m.currencies/keys [id]}]
          [::m.currencies/id id])
        (q.currencies/index-records))})

(defresolver currencies-link-resolver
  [_env {::m.currencies/keys [id]}]
  {::pc/input  #{::m.currencies/id}
   ::pc/output [{::m.currencies/link [::m.currencies/id]}]}
  {::m.currencies/link [[::m.currencies/id id]]})

(defresolver currency-resolver
  [_env {::m.currencies/keys [name]}]
  {::pc/input  #{::m.currencies/name}
   ::pc/output [::m.currencies/name]}
  (when-let [record (q.currencies/read-record name)]
    (dissoc record :db/id)))

(defresolver account-currencies-resolver
  [_env _props]
  {::pc/input  #{::m.accounts/id}
   ::pc/output [{::m.accounts/currencies [::m.currencies/name]}]}
  {::m.accounts/currencies (q.currencies/index-records)})

(defresolver user-currencies-resolver
  [_env {::m.users/keys [id]}]
  {::pc/input  #{::m.users/id}
   ::pc/output [{::m.users/currencies [::m.currencies/id]}]}
  {::m.users/currencies (q.currencies/index-by-user id)})

(def resolvers
  [account-currencies-resolver
   currencies-resolver
   currencies-link-resolver
   currency-resolver
   user-currencies-resolver])
