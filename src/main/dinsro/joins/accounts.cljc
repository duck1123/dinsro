(ns dinsro.joins.accounts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.models :as m.models :refer [defmodel]]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [dinsro.queries.debits :as q.debits])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   [dinsro.specs]))

;; [[../model/accounts.cljc]]
;; [[../ui/accounts.cljs]]

(comment ::m.c.wallets/_ ::m.rate-sources/_)

(def model-key ::m.accounts/id)

(def join-info
  (merge
   {:model-key model-key
    :idents m.accounts/idents}
   #?(:clj {:indexer q.accounts/index-ids
            :counter q.accounts/count-ids})))

(defmodel model-key
  {::m.models/name "Accounts"})

(defattr admin-index ::admin-index :ref
  {ao/target     model-key
   ao/pc-output  [{::admin-index [:total {:results [model-key]}]}]
   ao/pc-resolve (fn [env props] {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr flat-admin-index ::flat-admin-index :ref
  {ao/target     model-key
   ao/pc-output  [{::flat-admin-index [model-key]}]
   ao/pc-resolve (fn [env props] {::flat-admin-index (j/make-flat-admin-indexer join-info env props)})})

(defattr flat-index ::flat-index :ref
  {ao/target    model-key
   ao/pc-output [{::flat-index [model-key]}]
   ao/pc-resolve (fn [env props] {::flat-index (j/make-flat-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    model-key
   ao/pc-output [{::index [:total {:results [model-key]}]}]
   ao/pc-resolve (fn [env props] {::index (j/make-indexer join-info env props)})})

(defattr debit-count
  "Count of debits associated with account"
  ::debit-count :number
  {ao/pc-input #{::debits}
   ao/pc-resolve (fn [_ {::keys [debits]}] {::debit-count (count debits)})})

(defattr debits
  "Index debits associated with an account"
  ::debits :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.accounts/id}
   ao/pc-output   [{::debits [::m.debits/id]}]
   ao/target      ::m.debits/id
   ao/pc-resolve
   (fn [_env {::m.accounts/keys [id]}]
     (let [ids #?(:clj (q.debits/index-ids {::m.debits/id id})
                  :cljs (do (comment id) []))]
       {::debits (m.debits/idents ids)}))})

(defattr transactions
  "Index transactions associated with an account"
  ::transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.accounts/id}
   ao/pc-output   [{::transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.accounts/keys [id]}]
     #?(:clj  (if id
                (let [ids (q.transactions/index-ids {model-key id})]
                  {::transactions (m.transactions/idents ids)})
                {::transactions []})
        :cljs (comment id)))})

(defattr transaction-count
  "Count of transactions associated with an account"
  ::transaction-count :number
  {ao/pc-input   #{::transactions}
   ao/pc-resolve (fn [_ {::keys [transactions]}] {::transaction-count (count transactions)})})

(def attributes
  [admin-index debits debit-count
   flat-index flat-admin-index
   index transaction-count transactions])
