(ns dinsro.resolvers.transactions
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))

(defn resolve-transaction
  [id]
  (get sample/transaction-map id))

(defn resolve-transaction-link
  [id]
  {::m.transactions/link [(m.transactions/ident id)]})

(defn resolve-transactions
  []
  (let [ids (keys sample/transaction-map)
        idents (map m.transactions/ident ids)]
    {:all-transactions idents}))

(defresolver transaction-resolver
  [_env {::m.transactions/keys [id]}]
  {::pc/input  #{::m.transactions/id}
   ::pc/output [{::m.transactions/account [::m.accounts/id]}
                ::m.transactions/date
                ::m.transactions/description]}
  (resolve-transaction id))

(defresolver transaction-link-resolver
  [_env {::m.transactions/keys [id]}]
  {::pc/input  #{::m.transactions/id}
   ::pc/output [{::m.transactions/link [::m.transactions/id]}]}
  (resolve-transaction-link id))

(defresolver transactions-resolver
  [_env _props]
  {::pc/output [{:all-transactions [::m.transactions/id]}]}
  (resolve-transactions))

(def resolvers
  [transaction-resolver
   transaction-link-resolver
   transactions-resolver])
