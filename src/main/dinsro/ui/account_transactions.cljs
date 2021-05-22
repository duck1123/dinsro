(ns dinsro.ui.account-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-account-transaction :as u.f.add-account-transaction]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc AccountTransactions
  [this {::keys            [form toggle-button]
         ::m.accounts/keys [transactions]}]
  {:componentDidMount
   (fn [this]
     (uism/begin! this machines/hideable form-toggle-sm {:actor/navbar this}))
   :ident         ::m.accounts/id
   :initial-state {::toggle-button           {:form-button/id form-toggle-sm}
                   ::form                    {:form-button/id form-toggle-sm}
                   ::m.accounts/id           nil
                   ::m.accounts/transactions []}
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (let [defaults    {::form          {}
                                       ::toggle-button {:form-button/id form-toggle-sm}}
                          merged-data (merge defaults current-normalized data-tree)]
                      merged-data))
   :query         [::m.accounts/id
                   {::form (comp/get-query u.f.add-account-transaction/AddAccountTransactionForm)}
                   {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                   {::m.accounts/transactions (comp/get-query u.index-transactions/IndexTransactionLine)}
                   [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/container
     (bulma/box
      (dom/h2 {}
        (tr [:transactions])
        (if toggle-button
          (u.buttons/ui-show-form-button toggle-button)
          (dom/p "not loaded")))
      (if shown?
        (u.f.add-account-transaction/ui-add-account-transaction-form form)
        (dom/p "not loaded"))
      (dom/hr)
      (if (seq transactions)
        (dom/div {}
          (map u.index-transactions/ui-index-transaction-line transactions))
        (dom/p "no items"))))))

(def ui-account-transactions (comp/factory AccountTransactions))
