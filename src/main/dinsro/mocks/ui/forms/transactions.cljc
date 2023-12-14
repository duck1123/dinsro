(ns dinsro.mocks.ui.forms.transactions
  (:require
   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.currencies :as o.currencies]
   [dinsro.options.debits :as o.debits]
   [dinsro.options.transactions :as o.transactions]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

;; [[../../../ui/forms/transactions.cljc]]
;; [[../../../../../test/dinsro/ui/forms/transactions_test.cljs]]

(def account-count 2)
(def debit-count 2)
(def enable-debug? false)

(defn create-transaction-currency-data
  [_opts]
  {o.currencies/id   (ds/gen-key o.currencies/id)
   o.currencies/name (ds/gen-key o.currencies/name)})

(defn create-transaction-debit-account-data
  [opts]
  (let [currency (:currency opts (create-transaction-currency-data {}))]
    {:position           (:position opts 0)
     o.accounts/name     (ds/gen-key o.accounts/name)
     o.accounts/currency currency
     :ui/debug-props?    false}))

(defn transaction-debit-data
  [_]
  {})

(defn create-transaction-accounts
  [_opts]
  (mapv (fn [i] (create-transaction-debit-account-data {:position i}))
        (range account-count)))

(defn CreateTransactionDebitLine-data
  [opts]
  (log/info :CreateTransactionDebitLine-data/starting {:opts opts})
  (let [accounts (:accounts opts (create-transaction-accounts {}))]
    {o.debits/id      (ds/gen-key o.debits/id)
     :position        (:position opts 2)
     :value           (:value opts 72)
     :ui/debug-props? false
     :ui/form-state   {:accounts accounts}}))

(defn CreateTransactionForm-data
  [opts]
  (log/info :CreateTransactionForm-data/starting {:opts opts})
  (let [{:ui/keys [debug-props?]} opts]
    {:component/id              :dinsro.ui.forms.transactions/CreateTransactionForm
     o.transactions/description (ds/gen-key o.transactions/description)
     o.transactions/date        (ds/gen-key o.transactions/date)
     o.transactions/id          (ds/gen-key o.transactions/id)
     :ui/debug-props?           (and enable-debug? debug-props?)
     :ui/form-state
     {:debits
      (mapv (fn [i] (CreateTransactionDebitLine-data {:position i}))
            (range debit-count))}}))

(defn NewDebit-data
  [_a]
  {o.transactions/description (ds/gen-key o.transactions/description)
   o.transactions/date        (ds/gen-key o.transactions/date)
   o.transactions/id          (ds/gen-key o.transactions/id)
   ::j.transactions/debits    (map transaction-debit-data (range debit-count))})

(defn NewTransaction-data
  [_a]
  (let [transaction-id (ds/gen-key o.transactions/id)]
    {o.transactions/description (ds/gen-key o.transactions/description)
     o.transactions/date        (ds/gen-key o.transactions/date)
     o.transactions/id          transaction-id
     ::j.transactions/debits    (map transaction-debit-data (range debit-count))
     :com.fulcrologic.rad.picker-options/options-cache
     {:dinsro.joins.accounts/flat-index
      {:cached-at    1703595745553
       :options      []
       :query-result []}}
     ::fs/config
     {::fs/complete?      #{o.transactions/description o.transactions/date}
      ::fs/id             [o.transactions/id transaction-id]
      ::fs/fields         #{o.transactions/description o.transactions/date}
      ::fs/pristine-state {}
      ::fs/subforms       {::j.transactions/debits [(transaction-debit-data 0)]}}}))

(defn EditForm-data
  [_a]
  {o.transactions/description (ds/gen-key o.transactions/description)
   o.transactions/date        (ds/gen-key o.transactions/date)
   o.transactions/id          (ds/gen-key o.transactions/id)
   ::j.transactions/debits    (map transaction-debit-data (range debit-count))})
