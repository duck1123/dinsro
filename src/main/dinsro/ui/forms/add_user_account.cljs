(ns dinsro.ui.forms.add-user-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations :as mutations]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AddUserAccountForm
  [this {::keys [currency name initial-value submit]}]
  {:ident (fn [] [:component/id ::form])
   :initial-state {::currency      {:label (tr [:currency])}
                   ::initial-value 0
                   ::name          ""
                   ::submit        {:label (tr [:submit])}}
   :query [{::currency      (comp/get-query u.inputs/CurrencySelector)}
           ::initial-value
           ::name
           {::submit        (comp/get-query u.inputs/PrimaryButton)}]}
  (dom/div
   (bulma/field
    (bulma/control
     (u.inputs/ui-text-input
      {:label (tr [:name]) :value name}
      {:onChange #(fm/set-string! this ::name :event %)})))
   (bulma/field
    (bulma/control
     (u.inputs/ui-number-input
      {:label (tr [:initial-value]) :value initial-value}
      {:onChange #(fm/set-string! this ::initial-value :event %)})))
   (bulma/field
    (bulma/control
     (u.inputs/ui-currency-selector
      currency
      {:onChange #(fm/set-string! this ::currency :event %)})))
   (bulma/field
    (bulma/control
     (u.inputs/ui-primary-button
      submit
      {:onClick
       (fn [_]
         (timbre/info "click")
         (let [data {::m.accounts/currency {::m.currencies/id currency}
                     ::m.accounts/name name
                     ::m.accounts/initial-value initial-value}]
           (comp/transact! this [(mutations/submit data)])))})))))

(def ui-form (comp/factory AddUserAccountForm))
