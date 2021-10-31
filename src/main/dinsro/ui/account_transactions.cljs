(ns dinsro.ui.account-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as log]))

(defsc AccountTransactions
  [_this {::m.accounts/keys [transactions]}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/id           nil
                   ::m.accounts/transactions []}
   :query         [::m.accounts/id
                   {::m.accounts/transactions (comp/get-query u.index-transactions/IndexTransactionLine)}]}
  (bulma/container
   (bulma/box
    (dom/h2 {}
      (tr [:transactions]))
    (dom/hr)
    (if (seq transactions)
      (dom/div {}
        (map u.index-transactions/ui-index-transaction-line transactions))
      (dom/p "no items")))))

(def ui-account-transactions (comp/factory AccountTransactions))
