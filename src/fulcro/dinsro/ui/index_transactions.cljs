(ns dinsro.ui.index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexTransactionLine
  [_this {::m.transactions/keys [date description account-id]}]
  {:css [[:.card {:margin-bottom "5px"}]]
   :ident ::m.transactions/id
   :initial-state {::m.transactions/id          0
                   ::m.transactions/date        ""
                   ::m.transactions/description ""
                   ::m.transactions/account-id  0}
   :query [::m.transactions/account-id
           ::m.transactions/date
           ::m.transactions/description
           ::m.transactions/id]}
  (dom/div
   :.card
   (dom/div
    :.card-content
    (dom/div
     :.level.is-mobile
     (dom/div
      :.level-left
      (dom/div
       :.level-item
       (dom/p description))))
    (dom/div
     :.level.is-mobile
     (dom/div
      :.level-left
      (dom/div
       :.level-item
       date))
     (dom/div
      :.level-right
      (dom/div
       :.level-item
       account-id))))
   (dom/footer
    :.card-footer
    (dom/a
     :.button.card-footer-item
     {:onClick (fn [_] (timbre/info "delete"))}
     (tr [:delete])))))

(def ui-index-transaction-line
  (comp/factory IndexTransactionLine {:keyfn ::m.transactions/id}))

(defsc IndexTransactions
  [_this {::keys [transactions]}]
  {:initial-state {::transactions []}
   :query [{::transactions (comp/get-query IndexTransactionLine)}]}
  (if (seq transactions)
    (dom/div
     (map ui-index-transaction-line transactions))
    (dom/p "no items")))

(def ui-index-transactions
  (comp/factory IndexTransactions))
