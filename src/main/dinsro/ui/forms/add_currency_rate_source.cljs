(ns dinsro.ui.forms.add-currency-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations :as mutations]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as log]))

(defsc AddCurrencyRateSourceForm
  [this {::keys [name submit]}]
  {:ident (fn [] [:component/id ::form])
   :initial-state {::name ""}
   :query [::name
           {::submit (comp/get-query u.inputs/PrimaryButton)}]}
  (dom/div
    (u.inputs/ui-text-input
     {:label (tr [:name]) :value name}
     {:onChange #(fm/set-string! this ::name :event %)})
    (bulma/field
     (bulma/control
      (u.inputs/ui-primary-button
       submit
       {:onClick
        (fn [_]
          (let [data {::m.rate-sources/name name}]
            (comp/transact! this [(mutations/submit data)])))})))))

(def ui-form (comp/factory AddCurrencyRateSourceForm))
