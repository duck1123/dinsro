(ns dinsro.ui.rate-source-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-transaction :as u.f.admin-create-transaction]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc RateSourceTransactions
  [this {::keys [form toggle-button transactions]}]
  {:componentDidMount #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar RateSourceTransactions})
   :ident (fn [_] [:component/id ::RateSourceTransactions])
   :initial-state {::m.rate-sources/id 0
                   ::form          {}
                   ::toggle-button {:form-button/id form-toggle-sm}
                   ::transactions  {}}
   :query [::m.rate-sources/id
           {::form          (comp/get-query u.f.admin-create-transaction/AdminCreateTransactionForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
           {::transactions  (comp/get-query u.index-transactions/IndexTransactions)}
           [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h2
       :.title.is-2
       (tr [:transactions])
       (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.admin-create-transaction/ui-admin-create-transaction-form form))
     (dom/hr)
     (if (seq transactions)
       (dom/div
         (u.index-transactions/ui-index-transactions transactions))
       (dom/p "No data")))))

(def ui-rate-source-transactions (comp/factory RateSourceTransactions))
