(ns dinsro.ui.account-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-account-transaction :as u.f.add-account-transaction]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as timbre]))

(defsc AccountTransactions
  [_this {::keys [form toggle-button transactions]}]
  {:initial-state {::form          {}
                   ::toggle-button {}
                   ::transactions  {}}
   :query [{::toggle-button (comp/get-query u.buttons/ShowFormButton)}
           {::form          (comp/get-query u.f.add-account-transaction/AddAccountTransactionForm)}
           {::transactions  (comp/get-query u.index-transactions/IndexTransactions)}]}
  (bulma/container
   (bulma/box
    (dom/h2
     (tr [:transactions])
     (u.buttons/ui-show-form-button toggle-button))
    (u.f.add-account-transaction/ui-add-account-transaction-form form)
    (dom/hr)
    (u.index-transactions/ui-index-transactions transactions))))

(def ui-account-transactions (comp/factory AccountTransactions))
