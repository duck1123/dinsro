(ns dinsro.ui.forms.admin-create-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AdminCreateRateSourceForm
  [_this _props]
  {:initial-state {}
   :query []}
  (dom/div
   :.box
   "Admin Create Rate Source"
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-admin-create-rate-source-form (comp/factory AdminCreateRateSourceForm))
