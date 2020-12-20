(ns dinsro.views.index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-transaction :as u.f.create-transaction]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as timbre]))

(defsc IndexTransactionsPage
  [_this {::keys [button-data form-data transactions]}]
  {:ident (fn [] [:page/id ::page])
   :initial-state {::button-data  {}
                   ::form-data    {}
                   ::transactions {}}
   :query [{::button-data  (comp/get-query u.buttons/ShowFormButton)}
           {::form-data    (comp/get-query u.f.create-transaction/CreateTransactionForm)}
           :page/id
           {::transactions (comp/get-query u.index-transactions/IndexTransactions)}]
   :route-segment ["transactions"]
   :will-enter
   (fn [app _props]
     (df/load! app :all-transactions u.index-transactions/IndexTransactionLine
               {:target [:page/id
                         ::page
                         ::transactions
                         ::u.index-transactions/transactions]})
     (dr/route-immediate (comp/get-ident IndexTransactionsPage {})))}
  (let [shown? false]
    (bulma/section
     (bulma/container
      (bulma/content
       (bulma/box
        (dom/h1
         (tr [:index-rates "Index Transactions"])
         (u.buttons/ui-show-form-button button-data))
        (when shown?
          (u.f.create-transaction/ui-create-transaction-form form-data))
        (dom/hr)
        (u.index-transactions/ui-index-transactions transactions)))))))

(def ui-page (comp/factory IndexTransactionsPage))
