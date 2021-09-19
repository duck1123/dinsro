(ns dinsro.views.index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [taoensso.timbre :as log]))

(defsc IndexTransactionsPage
  [_this {::keys [transactions]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :session/current-user-ref
               u.user-transactions/UserTransactions
               {:target [:page/id
                         ::page
                         ::transactions]}))
   :ident         (fn [] [:page/id ::page])
   :initial-state {::transactions {}}
   :query         [:page/id
                   {::transactions (comp/get-query u.user-transactions/UserTransactions)}]
   :route-segment ["transactions"]}
  (bulma/page
   {:className "index-transactions-page"}
   (when (::m.users/id transactions)
     (u.user-transactions/ui-user-transactions transactions))))

(def ui-page (comp/factory IndexTransactionsPage))
