(ns dinsro.ui.currency-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(defsc IndexTransactionLine
  [_this {::m.transactions/keys [id link]}]
  {:ident         ::m.transactions/id
   :initial-state {::m.transactions/id   1
                   ::m.transactions/link {}}
   :query         [::m.transactions/id
                   {::m.transactions/link (comp/get-query u.links/TransactionLink)}]}
  (dom/tr {}
    (dom/td {} (str id))
    (dom/td {} (u.links/ui-transaction-link (first link)))
    (dom/td {} (u.buttons/ui-delete-transaction-button {::m.transactions/id id}))))

(def ui-index-transaction-line (comp/factory IndexTransactionLine {:keyfn ::m.transactions/id}))

(defsc IndexTransactions
  [_this {::keys [transactions]}]
  {:initial-state {::transactions []}
   :query         [{::transactions (comp/get-query IndexTransactionLine)}]}
  (if (seq transactions)
    (dom/table :.ui.table
      (dom/thead {}
        (dom/tr {}
          (dom/th {} "Id")
          (dom/th {} "initial value")
          (dom/th {} "Actions")))
      (dom/tbody {}
        (map ui-index-transaction-line transactions)))
    (dom/div {} (tr [:no-transactions]))))

(def ui-index-transactions (comp/factory IndexTransactions))

(defsc CurrencyTransactions
  [_this {::keys [transactions]}]
  {:ident         ::m.currencies/id
   :initial-state {::transactions  {}}
   :query         [::m.currencies/id
                   {::transactions (comp/get-query IndexTransactions)}]}
  (bulma/box
   (dom/h2 {}
     (tr [:transactions]))
   (dom/hr {})
   (ui-index-transactions transactions)))

(def ui-currency-transactions
  (comp/factory CurrencyTransactions))
