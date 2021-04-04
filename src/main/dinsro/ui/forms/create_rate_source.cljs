(ns dinsro.ui.forms.create-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.mutations :as mutations]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc CreateRateSourceForm
  [this {::keys [currency currency-id name url]}]
  {:ident (fn [] [:component/id ::form])
   :initial-state {::currency   {}
                   ::currency-id 0
                   ::name       ""
                   ::url        ""}
   :query [{::currency      (comp/get-query u.inputs/CurrencySelector)}
           ::currency-id
           ::name
           ::url]}
  (dom/div
   (bulma/field
    (bulma/control
     (u.inputs/ui-text-input
      {:label (tr [:name]) :value name}
      {:onChange #(fm/set-string! this ::name :event %)})))
   (bulma/field
    (bulma/control
     (u.inputs/ui-text-input
      {:label (tr [:url]) :value url}
      {:onChange #(fm/set-string! this ::url :event %)})))
   (bulma/field
    (bulma/control
     (u.inputs/ui-currency-selector
      currency
      {:onChange #(fm/set-integer! this ::currency-id :event %)})))
   (bulma/field
    (bulma/control
     (u.inputs/ui-primary-button
      {}
      {:onClick
       (fn []
         (let [data {:name name
                     :currency-id currency-id
                     :url url}]
           (comp/transact! this [(mutations/create-rate-source data)])))})))))

(def ui-create-rate-source-form (comp/factory CreateRateSourceForm))
