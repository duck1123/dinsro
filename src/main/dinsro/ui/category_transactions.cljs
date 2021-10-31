(ns dinsro.ui.category-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(defsc IndexCategoryTransactionLine
  [_this {::m.transactions/keys [account date description id]}]
  {:css           [[:.card {:margin-bottom "5px"}]]
   :ident         ::m.transactions/id
   :initial-state {::m.transactions/id          nil
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
          (dom/div :.level-item date))
        (dom/div :.level-right
          (dom/div :.level-item (u.links/ui-account-link account)))))
    (dom/footer :.card-footer
      (u.buttons/ui-delete-transaction-button {::m.transactions/id id}))))

(def ui-index-category-transaction-line
  (comp/factory IndexCategoryTransactionLine {:keyfn ::m.transactions/id}))

(defsc IndexCategoryTransactions
  [_this _]
  {:initial-state {::transactions []}
   :query         [{::transactions (comp/get-query IndexCategoryTransactionLine)}]})

(def ui-index-category-transactions
  (comp/factory IndexCategoryTransactions))

(defsc CategoryTransactions
  [_this {::m.categories/keys [transactions]}]
  {:ident         ::m.categories/id
   :initial-state {::m.categories/id           nil
                   ::m.categories/transactions {}}
   :query         [::m.categories/id
                   {::m.categories/transactions (comp/get-query IndexCategoryTransactions)}]}
  (bulma/box
   (dom/h2 {}
     "Transactions")
   (when transactions
     (if (seq transactions)
       (dom/div {}
         (map ui-index-category-transaction-line transactions))
       (dom/p "no items")))))

(def ui-category-transactions
  (comp/factory CategoryTransactions))
