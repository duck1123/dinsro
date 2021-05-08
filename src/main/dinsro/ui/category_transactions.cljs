(ns dinsro.ui.category-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-category-transaction :as u.f.add-category-transaction]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc IndexCategoryTransactionLine
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
          (dom/div :.level-item date))
        (dom/div :.level-right
          (dom/div :.level-item (u.links/ui-account-link account)))))
    (dom/footer :.card-footer
      (u.buttons/ui-delete-transaction-button {::m.transactions/id id}))))

(def ui-index-category-transaction-line
  (comp/factory IndexCategoryTransactionLine {:keyfn ::m.transactions/id}))

(defsc IndexCategoryTransactions
  [_this {::keys [transactions]}]
  {:initial-state {::transactions []}
   :query         [{::transactions (comp/get-query IndexCategoryTransactionLine)}]}
  (if (seq transactions)
    (dom/div {}
      (map ui-index-category-transaction-line transactions))
    (dom/p "no items")))

(def ui-index-category-transactions
  (comp/factory IndexCategoryTransactions))

(defsc CategoryTransactions
  [this {::keys [form transactions toggle-button] :as props}]
  {:componentDidMount
   #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar CategoryTransactions})
   :ident         (fn [] [:component/id ::CategoryTransactions])
   :initial-state {::form          {}
                   ::toggle-button {:form-button/id form-toggle-sm}
                   ::transactions  {}}
   :query         [{::form (comp/get-query u.f.add-category-transaction/AddCategoryTransactionForm)}
                   {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                   {::transactions (comp/get-query IndexCategoryTransactions)}
                   [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (js/console.log props)
    (bulma/box
     (dom/h2 {}
       "Transactions"
       (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.add-category-transaction/ui-form form))
     (ui-index-category-transactions (timbre/spy :info transactions)))))

(def ui-category-transactions
  (comp/factory CategoryTransactions))
