(ns dinsro.ui.user-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
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
    (dom/td {} (u.links/ui-transaction-link link))
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

(defsc UserTransactions
  [_this {::m.users/keys [id transactions]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id           nil
                   ::m.users/transactions []}
   :query         [::m.users/id
                   {::m.users/transactions (comp/get-query IndexTransactionLine)}]}
  (if id
    (bulma/box
     (dom/h2 {}
       (tr [:transactions]))
     (dom/hr {})
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
    (dom/p {} "User Transactions Not loaded")))

(def ui-user-transactions
  (comp/factory UserTransactions))
