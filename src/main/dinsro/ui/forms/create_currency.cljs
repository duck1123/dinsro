(ns dinsro.ui.forms.create-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations :as mutations]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateCurrencyForm
  [this {::keys [name]}]
  {:ident (fn [] [:component/id ::form])
   :initial-state {::name ""}
   :query [::name]}
  (dom/div
   (u.inputs/ui-text-input
    {:label (tr [:name])
     :value name}
    {:onChange #(fm/set-string! this ::name :event %)})
   (u.inputs/ui-primary-button
    {}
    {:onClick #(comp/transact! this [(mutations/create-currency {::m.currencies/name name})])})))

(def ui-create-currency-form (comp/factory CreateCurrencyForm))
