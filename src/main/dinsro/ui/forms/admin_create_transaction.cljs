(ns dinsro.ui.forms.admin-create-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc AdminCreateTransactionForm
  [_this {::keys [description value]}]
  {:initial-state {::description ""
                   ::value       ""}
   :query [::description
           ::value]}
  (bulma/field-group
   (bulma/field
    (bulma/column
     (u.inputs/ui-text-input
      {:label (tr [:value]) :value value}
      {:onChange (fn [] (timbre/info "change"))})))
   (bulma/field
    (bulma/column
     (u.inputs/ui-text-input
      {:label (tr [:description]) :value description}
      {:onChange (fn [] (timbre/info "change"))})))))

(def ui-admin-create-transaction-form (comp/factory AdminCreateTransactionForm))
