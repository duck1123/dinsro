(ns dinsro.ui.category-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc IndexCategoryTransactionLine
  [_this {::m.transactions/keys [date description account]}]
  {:css [[:.card {:margin-bottom "5px"}]]
   :ident ::m.transactions/id
   :initial-state {::m.transactions/id          0
                   ::m.transactions/date        ""
                   ::m.transactions/description ""
                   ::m.transactions/account     {}}
   :query [::m.transactions/id
           ::m.transactions/date
           ::m.transactions/description
           {::m.transactions/account (comp/get-query u.links/AccountLink)}]}
  (dom/div
   :.card
   (dom/div
    :.card-content
    (dom/div
     :.level.is-mobile
     (dom/div
      :.level-left
      (dom/div :.level-item (dom/p description))))
    (dom/div
     :.level.is-mobile
     (dom/div
      :.level-left
      (dom/div :.level-item date))
     (dom/div
      :.level-right
      (dom/div :.level-item (u.links/ui-account-link account)))))
   (dom/footer
    :.card-footer
    (dom/a
     :.button.card-footer-item
     {:onClick (fn [_] (timbre/info "delete"))}
     (tr [:delete])))))

(def ui-index-category-transaction-line
  (comp/factory IndexCategoryTransactionLine {:keyfn ::m.transactions/id}))

(defsc IndexCategoryTransactions
  [_this {::keys [transactions]}]
  {:initial-state {::transactions []}
   :query [{::transactions (comp/get-query IndexCategoryTransactionLine)}]}
  (if (seq transactions)
    (dom/div
     (map ui-index-category-transaction-line transactions))
    (dom/p "no items")))

(def ui-index-category-transactions
  (comp/factory IndexCategoryTransactions))

(defsc CategoryTransactions
  [_this {::keys [transactions]}]
  {:initial-state {::transactions []}
   :query [{::transactions (comp/get-query IndexCategoryTransactionLine)}]}
  (ui-index-category-transactions transactions))

(def ui-category-transactions
  (comp/factory CategoryTransactions))
