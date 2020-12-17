(ns dinsro.views.index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-transaction :as u.f.create-transaction]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as timbre]))

(defsc IndexTransactionsPage
  [_this {::keys [button-data form-data transactions]}]
  {:query [:page/id
           {::button-data (comp/get-query u.buttons/ShowFormButton)}
           {::form-data (comp/get-query u.f.create-transaction/CreateTransactionForm)}
           {::transactions (comp/get-query u.index-transactions/IndexTransactions)}]
   :ident (fn [] [:page/id ::page])
   :initial-state
   (fn [_]
     {::button-data (comp/get-initial-state u.buttons/ShowFormButton)
      ::form-data (comp/get-initial-state u.f.create-transaction/CreateTransactionForm)
      ::transactions (comp/get-initial-state u.index-transactions/IndexTransactions)})
   :route-segment ["transactions"]}
  (bulma/section
   (bulma/container
    (bulma/content
     (bulma/box
      (dom/h1
       (tr [:index-rates "Index Transactions"])
       (u.buttons/ui-show-form-button button-data))
      (u.f.create-transaction/ui-create-transaction-form form-data)
      (dom/hr)
      (u.index-transactions/ui-index-transactions transactions))))))

(def ui-page (comp/factory IndexTransactionsPage))
