(ns dinsro.ui.forms.create-rate
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.datepicker :as u.datepicker]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateRateForm
  [_this {::keys [close-button datepicker]}]
  {:query [{::close-button (comp/get-query u.buttons/CloseButton)}
           {::datepicker (comp/get-query u.datepicker/Datepicker)}]
   :initial-state
   (fn [_state]
     {::close-button (comp/get-initial-state u.buttons/CloseButton)
      ::datepicker (comp/get-initial-state u.datepicker/Datepicker)})}
  (dom/div
   (u.buttons/ui-close-button close-button)
   (u.datepicker/ui-datepicker datepicker)

   "Create Rate"
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-create-rate-form (comp/factory CreateRateForm))
