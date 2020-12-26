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
  [_this {::keys [button-data form-data transaction-data]}]
  {:initial-state {::button-data      {}
                   ::form-data        {}
                   ::transaction-data {}}
   :query [{::button-data      (comp/get-query u.buttons/ShowFormButton)}
           {::form-data        (comp/get-query u.f.add-account-transaction/AddAccountTransactionForm)}
           {::transaction-data (comp/get-query u.index-transactions/IndexTransactions)}]}
  (bulma/container
   (bulma/box
    (dom/h2 (tr [:transactions])
            (u.buttons/ui-show-form-button button-data))
    (u.f.add-account-transaction/ui-add-account-transaction-form form-data)
    (dom/hr)
    (u.index-transactions/ui-index-transactions transaction-data))))

(def ui-account-transactions (comp/factory AccountTransactions))
