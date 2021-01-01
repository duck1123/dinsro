(ns dinsro.ui.forms.add-account-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.datepicker :as u.datepicker]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AddAccountTransactionForm
  [_this _props]
  {:query []}
  (dom/div
   (dom/div
    :.field
    (dom/div
     :.control
     (u.inputs/ui-text-input)))
   (dom/div
    :.field
    (dom/div
     :.control
     (u.inputs/ui-number-input)))
   (dom/div
    :.field
    (dom/div
     :.control
     (u.datepicker/ui-datepicker)))
   (dom/div
    :.field
    (dom/div
     :.control
     (u.inputs/ui-primary-button)))))

(def ui-add-account-transaction-form
  (comp/factory AddAccountTransactionForm))
