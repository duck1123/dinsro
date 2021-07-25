(ns dinsro.ui.category-transactions-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.sample :as sample]
   [dinsro.ui.category-transactions :as u.category-transactions]
   [dinsro.ui.forms.add-category-transaction :as u.f.add-category-transaction]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard CategoryTransactions
  {::wsm/card-height 12
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.category-transactions/CategoryTransactions
    ::ct.fulcro3/initial-state
    (fn []
      {::u.category-transactions/form
       (comp/get-initial-state u.f.add-category-transaction/AddCategoryTransactionForm)

       ::u.category-transactions/transactions
       {::u.category-transactions/transactions (vals sample/transaction-map)}

       ::u.category-transactions/toggle-button
       {:form-button/id ::u.category-transactions/form-toggle}

       ::uism/asm-id ::u.category-transactions/form-toggle})}))
