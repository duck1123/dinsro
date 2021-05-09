(ns dinsro.ui.forms.create-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations.currencies :as mu.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as log]))

(defsc CreateCurrencyForm
  [this {::keys [id name]}]
  {:ident         (fn [] [:component/id ::form])
   :initial-state {::id   ""
                   ::name ""}
   :query         [::id ::name]}
  (dom/div
    (u.inputs/ui-text-input
     {:label (tr [:id])
      :value id}
     {:onChange #(fm/set-string! this ::id :event %)})
    (u.inputs/ui-text-input
     {:label (tr [:name])
      :value name}
     {:onChange #(fm/set-string! this ::name :event %)})
    (u.inputs/ui-primary-button
     {}
     {:onClick #(comp/transact! this [(mu.currencies/create! {::m.currencies/id   id
                                                              ::m.currencies/name name})])})))

(def ui-create-currency-form (comp/factory CreateCurrencyForm))
