(ns dinsro.ui.forms.create-rate
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateRateForm
  [_this _props]
  {:query []}
  (dom/div
   (u.buttons/ui-close-button #_close-button)

   "Create Rate"
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-create-rate-form (comp/factory CreateRateForm))
