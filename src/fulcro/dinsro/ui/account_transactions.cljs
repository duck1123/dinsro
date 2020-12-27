(ns dinsro.ui.account-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-account-transaction :as u.f.add-account-transaction]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc AccountTransactions
  [this {::keys [form toggle-button transactions]}]
  {:componentDidMount
   (fn [this]
     (uism/begin! this machines/hideable form-toggle-sm {:actor/navbar AccountTransactions}))
   :ident (fn [] [:component/id ::AccountTransactions])
   :initial-state {::toggle-button {:form-button/id form-toggle-sm}
                   ::form          {:form-button/id form-toggle-sm}
                   ::transactions  {}}
   :query [{::form          (comp/get-query u.f.add-account-transaction/AddAccountTransactionForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
           {::transactions  (comp/get-query u.index-transactions/IndexTransactions)}
           [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/container
     (bulma/box
      (dom/h2
       (tr [:transactions])
       (u.buttons/ui-show-form-button toggle-button))
      (when shown?
        (u.f.add-account-transaction/ui-add-account-transaction-form form))
      (dom/hr)
      (u.index-transactions/ui-index-transactions transactions)))))

(def ui-account-transactions (comp/factory AccountTransactions))
