(ns dinsro.ui.forms.create-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateRateSourceForm
  [_this _props]
  {:initial-state {}
   :query []}
  (dom/div
   :.box
   (u.buttons/ui-close-button #_close-button)

   "Create Rate Source"
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-create-rate-source-form (comp/factory CreateRateSourceForm))
