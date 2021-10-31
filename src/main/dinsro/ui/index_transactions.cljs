(ns dinsro.ui.index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [taoensso.timbre :as log]))

(defsc IndexTransactionLine
  [_this {::m.transactions/keys [account date description id]}]
  {:css           [[:.card {:margin-bottom "5px"}]]
   :ident         ::m.transactions/id
   :initial-state {::m.transactions/id          0
                   ::m.transactions/date        ""
                   ::m.transactions/description ""
                   ::m.transactions/account     {}}
   :query         [::m.transactions/id
                   ::m.transactions/date
                   ::m.transactions/description
                   {::m.transactions/account (comp/get-query u.links/AccountLink)}]}
  (dom/div :.card
    (dom/div :.card-content
      (dom/div :.level.is-mobile
        (dom/div :.level-left
          (dom/div :.level-item (dom/p description))))
      (dom/div :.level.is-mobile
        (dom/div :.level-left
          (dom/div :.level-item (str date)))
        (dom/div :.level-right
          (dom/div :.level-item (u.links/ui-account-link account)))))
    (dom/footer :.card-footer
      (u.buttons/ui-delete-transaction-button {::m.transactions/id id}))))

(def ui-index-transaction-line
  (comp/factory IndexTransactionLine {:keyfn ::m.transactions/id}))

(defsc IndexTransactions
  [_this {::keys [transactions]}]
  {:initial-state {::transactions []}
   :query         [{::transactions (comp/get-query IndexTransactionLine)}]}
  (if (seq transactions)
    (dom/div {}
      (map ui-index-transaction-line transactions))
    (dom/p "no items")))

(def ui-index-transactions
  (comp/factory IndexTransactions))

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
