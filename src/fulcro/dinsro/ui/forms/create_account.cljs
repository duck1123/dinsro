(ns dinsro.ui.forms.create-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateAccountForm
  [_this _props]
  {:query []}
  (dom/div
   (u.buttons/ui-close-button #_close-button)

   "Create Account"
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-create-account-form (comp/factory CreateAccountForm))
