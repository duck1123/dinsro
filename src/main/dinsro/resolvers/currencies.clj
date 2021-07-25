(ns dinsro.resolvers.currencies
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.currencies :as q.currencies]
   [taoensso.timbre :as log]))

(def sats {::m.currencies/id   "sats"
           ::m.currencies/name "Sats"})

(defn resolve-currencies
  []
  (let [records (q.currencies/index-records)
        idents  (map m.currencies/ident-item records)]
    {:all-currencies idents}))

(defn resolve-currency
  [id]
  (when-let [eid (q.currencies/find-eid-by-id id)]
    (when-let [record (q.currencies/read-record eid)]
      (dissoc record :db/id))))

(>defn resolve-currency-link
  [id]
  [::m.currencies/id => (s/keys)]
  {::m.currencies/link [(m.currencies/ident id)]})

(defn resolve-user-currencies
  [username]
  (let [currencies (q.currencies/index-by-user username)
        idents     (map m.currencies/ident-item currencies)]
    {::m.users/currencies idents}))

(defresolver currencies-resolver
  [_env _props]
  {::pc/output [{:all-currencies [::m.currencies/name]}]}
  (resolve-currencies))

(defresolver currencies-link-resolver
  [_env {::m.currencies/keys [id]}]
  {::pc/input  #{::m.currencies/id}
   ::pc/output [{::m.currencies/link [::m.currencies/id]}]}
  (resolve-currency-link id))

(defresolver currency-resolver
  [_env {::m.currencies/keys [id]}]
  {::pc/input  #{::m.currencies/id}
   ::pc/output [::m.currencies/name]}
  (resolve-currency id))

(defresolver account-currencies-resolver
  [_env _props]
  {::pc/input  #{::m.accounts/id}
   ::pc/output [{::m.accounts/currencies [::m.currencies/name]}]}
  {::m.accounts/currencies (q.currencies/index-records)})

(defresolver user-currencies-resolver
  [_env {::m.users/keys [id]}]
  {::pc/input  #{::m.users/id}
   ::pc/output [{::m.users/currencies [::m.currencies/id]}]}
  (resolve-user-currencies id))

(def resolvers
  [account-currencies-resolver
   currencies-resolver
   currencies-link-resolver
   currency-resolver
   user-currencies-resolver])
