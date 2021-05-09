(ns dinsro.views.index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [taoensso.timbre :as log]))

(defsc IndexTransactionsPage
  [_this {::keys [transactions]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :all-transactions u.user-transactions/IndexTransactionLine
               {:target [:component/id
                         ::u.user-transactions/UserTransactions
                         ::u.user-transactions/transactions
                         ::u.user-transactions/transactions]}))
   :ident (fn [] [:page/id ::page])
   :initial-state {::transactions {}}
   :query [:page/id
           {::transactions (comp/get-query u.user-transactions/UserTransactions)}]
   :route-segment ["transactions"]}
  (bulma/page
   (u.user-transactions/ui-user-transactions transactions)))

(def ui-page (comp/factory IndexTransactionsPage))
