(ns dinsro.ui.forms.admin-create-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AdminCreateAccountForm
  [_this _props]
  {:initial-state {}
   :query []}
  (dom/div
   (u.buttons/ui-close-button)
   (u.inputs/ui-text-input)
   (u.inputs/ui-text-input)
   (u.inputs/ui-currency-selector)
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-admin-create-account-form (comp/factory AdminCreateAccountForm))
