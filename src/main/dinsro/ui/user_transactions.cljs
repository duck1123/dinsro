(ns dinsro.ui.user-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-user-transaction :as u.f.add-user-transaction]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc IndexTransactionLine
  [_this {::m.transactions/keys [id link]}]
  {:ident         ::m.transactions/id
   :initial-state {::m.transactions/id   1
                   ::m.transactions/link {}}
   :query         [::m.transactions/id
                   {::m.transactions/link (comp/get-query u.links/TransactionLink)}]}
  (dom/tr {}
    (dom/td {} (str id))
    (dom/td {} (u.links/ui-transaction-link link))
    (dom/td {} (u.buttons/ui-delete-transaction-button {::m.transactions/id id}))))

(def ui-index-transaction-line (comp/factory IndexTransactionLine {:keyfn ::m.transactions/id}))

(defsc IndexTransactions
  [_this {::keys [transactions]}]
  {:initial-state {::transactions []}
   :query         [{::transactions (comp/get-query IndexTransactionLine)}]}
  (if (seq transactions)
    (dom/table :.ui.table
      (dom/thead {}
        (dom/tr {}
          (dom/th {} "Id")
          (dom/th {} "initial value")
          (dom/th {} "Actions")))
      (dom/tbody {}
        (map ui-index-transaction-line transactions)))
    (dom/div {} (tr [:no-transactions]))))

(def ui-index-transactions (comp/factory IndexTransactions))

(defsc UserTransactions
  [this {::m.users/keys [id transactions]
         ::keys         [form toggle-button]}]
  {:componentDidMount
   (fn [this]
     (uism/begin! this machines/hideable form-toggle-sm {:actor/navbar (uism/with-actor-class [::m.users/id :none]
                                                                         UserTransactions)}))
   :ident ::m.users/id
   :initial-state
   (fn [_]
     {::m.users/id           nil
      ::form                 (comp/get-initial-state u.f.add-user-transaction/AddUserTransactionForm)
      ::toggle-button        {:form-button/id form-toggle-sm}
      ::m.users/transactions []})
   :pre-merge
   (fn [{:keys [current-normalized data-tree]}]
     (let [defaults    {::form          (comp/get-initial-state u.f.add-user-transaction/AddUserTransactionForm)
                        ::toggle-button {:form-button/id form-toggle-sm}}
           merged-data (merge current-normalized data-tree defaults)]
       merged-data))
   :query [::m.users/id
           {::form (comp/get-query u.f.add-user-transaction/AddUserTransactionForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
           {::m.users/transactions (comp/get-query IndexTransactionLine)}
           [::uism/asm-id form-toggle-sm]]}
  (if id
    (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
      (bulma/box
       (dom/h2 {}
         (tr [:transactions])
         (when toggle-button (u.buttons/ui-show-form-button toggle-button)))
       (when shown?
         (when form (u.f.add-user-transaction/ui-form form)))
       (dom/hr {})
       (if (seq transactions)
         (dom/table :.ui.table
           (dom/thead {}
             (dom/tr {}
               (dom/th {} "Id")
               (dom/th {} "initial value")
               (dom/th {} "Actions")))
           (dom/tbody {}
             (map ui-index-transaction-line transactions)))
         (dom/div {} (tr [:no-transactions])))))
    (dom/p {} "User Transactions Not loaded")))

(def ui-user-transactions
  (comp/factory UserTransactions))
