(ns dinsro.ui.currency-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-currency-transaction :as u.f.add-currency-transaction]
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
    (dom/td {} (u.links/ui-transaction-link (first link)))
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

(defsc CurrencyTransactions
  [this {::keys [form toggle-button transactions]}]
  {:componentDidMount
   (fn [this]
     (uism/begin! this machines/hideable form-toggle-sm {:actor/navbar CurrencyTransactions}))
   :ident         ::m.currencies/id
   :initial-state {::form          {}
                   ::toggle-button {:form-button/id form-toggle-sm}
                   ::transactions  {}}
   :query         [::m.currencies/id
                   {::form (comp/get-query u.f.add-currency-transaction/AddCurrencyTransactionForm)}
                   {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                   {::transactions (comp/get-query IndexTransactions)}
                   [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h2 {}
       (tr [:transactions])
       (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.add-user-transaction/ui-form form))
     (dom/hr {})
     (ui-index-transactions transactions))))

(def ui-currency-transactions
  (comp/factory CurrencyTransactions))
