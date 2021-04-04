(ns dinsro.ui.forms.add-currency-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.mutations :as mutations]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AddCurrencyAccountForm
  [this {::keys [name initial-value]}]
  {:ident         (fn [] [:component/id ::form])
   :initial-state {::initial-value 0
                   ::name          "Foo"}
   :query         [::initial-value
                   ::name]}
  (dom/div
   (bulma/field
    (bulma/control
     (u.inputs/ui-text-input
      {:label (tr [:name]) :value (or name "")}
      {:onChange #(fm/set-string! this ::name :event %)})))
   (bulma/field
    (bulma/control
     (u.inputs/ui-number-input
      {:label (tr [:initial-value]) :value (str (or initial-value 0))}
      {:onChange #(fm/set-integer! this ::initial-value :event %)})))
   (bulma/field
    (bulma/control
     (u.inputs/ui-primary-button
      {}
      {:onClick
       (fn []
         (let [data {::m.accounts/name          name
                     ::m.accounts/initial-value initial-value}]
           (comp/transact! this [(mutations/create-account data)])))})))))

(def ui-form (comp/factory AddCurrencyAccountForm))
