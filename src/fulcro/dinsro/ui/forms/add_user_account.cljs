(ns dinsro.ui.forms.add-user-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AddUserAccountForm
  [_this _props]
  {:query []}
  (dom/div
   :.box
   (u.buttons/ui-close-button #_close-button)
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
     (u.inputs/ui-currency-selector)))
   (dom/div
    :.field
    (dom/div
     :.control
     (u.inputs/ui-primary-button)))

   "Add User Account"))

(def ui-form (comp/factory AddUserAccountForm))
