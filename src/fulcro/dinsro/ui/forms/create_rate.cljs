(ns dinsro.ui.forms.create-rate
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.datepicker :as u.datepicker]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateRateForm
  [_this {::keys [datepicker]}]
  {:initial-state {::datepicker {}}
   :query [{::datepicker (comp/get-query u.datepicker/Datepicker)}]}
  (dom/div
   (u.datepicker/ui-datepicker datepicker)
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-create-rate-form (comp/factory CreateRateForm))
