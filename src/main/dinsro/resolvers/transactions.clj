(ns dinsro.resolvers.transactions
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))

(defresolver transaction-resolver
  [_env {::m.transactions/keys [id]}]
  {::pc/input #{::m.transactions/id}
   ::pc/output [{::m.transactions/account [::m.accounts/id]}
                ::m.transactions/date
                ::m.transactions/description]}
  (get sample/transaction-map id))

(defresolver transaction-link-resolver
  [_env {::m.transactions/keys [id]}]
  {::pc/input #{::m.transactions/id}
   ::pc/output [{:dinsro.ui.links/link [::m.accounts/id]}]}
  {:dinsro.ui.links/link [[::m.transactions/id id]]})

(defresolver transactions-resolver
  [_env _props]
  {::pc/output [{:all-transactions [::m.transactions/id]}]}
  {:all-transactions
   (map (fn [id] [::m.transactions/id id]) (keys sample/transaction-map))})

(defresolver transaction-map-resolver
  [_env _props]
  {::pc/output [::m.transactions/map]}
  {::m.transactions/map sample/transaction-map})

(def resolvers
  [transaction-resolver
   transaction-link-resolver
   transactions-resolver
   transaction-map-resolver])
