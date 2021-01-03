(ns dinsro.ui.forms.create-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateAccountForm
  [this {::keys [name]}]
  {:ident (fn [] [:form/id ::form])
   :initial-state {::name ""}
   :query [::name]}
  (dom/div
   (u.inputs/ui-text-input
    {:label (tr [:name]) :value name}
    {:onChange #(fm/set-string! this ::name :event %)})))

(def ui-create-account-form (comp/factory CreateAccountForm))
