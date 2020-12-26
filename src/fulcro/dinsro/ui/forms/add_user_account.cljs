(ns dinsro.ui.forms.add-user-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AddUserAccountForm
  [_this {::keys [a b c close-button d]}]
  {:initial-state {::a            {}
                   ::b            {}
                   ::c            {}
                   ::close-button {}
                   ::d            {}}
   :query [{::a            (comp/get-query u.inputs/TextInput)}
           {::b            (comp/get-query u.inputs/NumberInput)}
           {::c            (comp/get-query u.inputs/CurrencySelector)}
           {::close-button (comp/get-query u.buttons/CloseButton)}
           {::d            (comp/get-query u.inputs/PrimaryButton)}]}
  (bulma/box
   (u.buttons/ui-close-button close-button)
   (bulma/field
    (bulma/control
     (u.inputs/ui-text-input a)))
   (bulma/field
    (bulma/control
     (u.inputs/ui-number-input b)))
   (bulma/field
    (bulma/control
     (u.inputs/ui-currency-selector c)))
   (bulma/field
    (bulma/control
     (u.inputs/ui-primary-button d)))

   "Add User Account"))

(def ui-form (comp/factory AddUserAccountForm))
