(ns dinsro.ui.forms.add-user-account
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

(defsc AddUserAccountForm
  [this {::keys [currency currency-id name initial-value submit]}]
  {:ident         (fn [] [:component/id ::form])
   :initial-state {::currency      {}
                   ::currency-id   0
                   ::initial-value 0
                   ::name          ""
                   ::submit        {:label (tr [:submit])}}
   :query         [{::currency (comp/get-query u.inputs/CurrencySelector)}
                   ::currency-id
                   ::initial-value
                   ::name
                   {::submit (comp/get-query u.inputs/PrimaryButton)}]}
  (dom/div
   (bulma/field
    (bulma/control
     (u.inputs/ui-text-input
      {:label (tr [:name]) :value name}
      {:onChange #(fm/set-string! this ::name :event %)})))
   (bulma/field
    (bulma/control
     (u.inputs/ui-number-input
      {:label (tr [:initial-value]) :value (str initial-value)}
      {:onChange #(fm/set-double! this ::initial-value :event %)})))
   (bulma/field
    (bulma/control
     (u.inputs/ui-currency-selector
      currency
      {:onChange #(fm/set-integer! this ::currency-id :event %)})))
   (bulma/field
    (bulma/control
     (u.inputs/ui-primary-button
      submit
      {:onClick
       (fn [_]
         (timbre/info "click")
         (let [data {::m.accounts/currency      {:db/id currency-id}
                     ::m.accounts/name          name
                     ::m.accounts/initial-value initial-value}]
           (comp/transact! this [(mutations/create-account data)])))})))))

(def ui-form (comp/factory AddUserAccountForm))
