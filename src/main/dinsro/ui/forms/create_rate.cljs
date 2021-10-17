(ns dinsro.ui.forms.create-rate
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as log]))

(defsc CreateRateForm
  [this {::m.rates/keys [name]}]
  {:ident (fn [] [:component/id ::form])
   :initial-state {::m.rates/name ""}
   :query [::m.rates/name]}
  (dom/div
    (u.inputs/ui-text-input
     {:label (tr [:name]) :value name}
     {:onChange #(fm/set-string! this ::m.rates/name :event %)})))

(def ui-create-rate-form (comp/factory CreateRateForm))
