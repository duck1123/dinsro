(ns dinsro.ui.forms.admin-create-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AdminCreateRateSourceForm
  [_this _props]
  {:initial-state {}
   :query []}
  (dom/div
   :.box
   (u.buttons/ui-close-button #_close-button)
   (u.inputs/ui-text-input)
   (u.inputs/ui-text-input)
   (u.inputs/ui-currency-selector)
   (dom/div
    :.field
    (dom/div
     :.control
     (u.inputs/ui-primary-button)))

   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-admin-create-rate-source-form (comp/factory AdminCreateRateSourceForm))
