(ns dinsro.ui.forms.create-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defn field-group
  [& body]
  (apply dom/div :.field-group body))

(defn field
  [& body]
  (apply dom/div :.field body))

(defn column
  [& body]
  (apply dom/div :.column body))

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
  (dom/div
   :.box
   "Create Transaction"
   (u.buttons/ui-close-button button)
   (field-group
    (field
     (column
      (dom/label :.label (tr [:value]))
      (u.inputs/ui-text-input value)))

    (field
     (column
      (u.inputs/ui-text-input description)))

    #_(u.inputs/ui-text-input {:label (tr [:name])}))))

(def ui-create-transaction-form (comp/factory CreateTransactionForm))
