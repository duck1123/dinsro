(ns dinsro.ui.forms.create-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateTransactionForm
  [_this _props]
  {:initial-state {}
   :query []}
  (dom/div
   :.box
   "Create Transaction"
   (u.buttons/ui-close-button)
   (dom/div
    :.field-group
    (dom/div
     :.field
     (dom/div
      :.column
      (dom/label
       :.label
       (tr [:value]))

      (dom/input
       :.input
       {:type :text})))

    (dom/div
     :.field
     (dom/div
      :.column
      (u.inputs/ui-text-input)))

    (u.inputs/ui-text-input {:label (tr [:name])}))))

(def ui-create-transaction-form (comp/factory CreateTransactionForm))
