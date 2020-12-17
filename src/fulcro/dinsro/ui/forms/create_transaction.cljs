(ns dinsro.ui.forms.create-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateTransactionForm
  [_this {::keys [button description value]}]
  {:query [{::button (comp/get-query u.buttons/CloseButton)}
           {::input (comp/get-query u.inputs/TextInput)}
           {::description (comp/get-query u.inputs/TextInput)}
           {::value (comp/get-query u.inputs/TextInput)}]
   :initial-state
   (fn [_]
     {::button (comp/get-initial-state u.buttons/CloseButton)
      ::input (comp/get-initial-state u.inputs/TextInput)
      ::description (comp/get-initial-state u.inputs/TextInput)
      ::value (comp/get-initial-state u.inputs/TextInput)})}
  (bulma/box
   "Create Transaction"
   (u.buttons/ui-close-button button)
   (bulma/field-group
    (bulma/field
     (bulma/column
      (dom/label :.label (tr [:value]))
      (u.inputs/ui-text-input value)))
    (bulma/field
     (bulma/column
      (u.inputs/ui-text-input description)))

    #_(u.inputs/ui-text-input {:label (tr [:name])}))))

(def ui-create-transaction-form (comp/factory CreateTransactionForm))
