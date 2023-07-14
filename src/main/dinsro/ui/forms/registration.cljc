(ns dinsro.ui.forms.registration
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [dinsro.mutations.session :as mu.session]
   [dinsro.ui.inputs :as u.inputs]))

(defsc RegistrationForm
  [this {::keys [confirm-password password username]}]
  {:ident         (fn [] [:component/id ::RegistrationForm])
   :initial-state {::confirm-password ""
                   ::password         ""
                   ::username         ""}
   :query         [:component/id
                   ::confirm-password
                   ::password
                   ::username]}
  (ui-form {}
    (ui-form-input
     {:value    username
      :onChange #(fm/set-string! this ::username :event %)
      :label    "Username"
      :error    false})
    (ui-form-input
     {:value    password
      :onChange #(fm/set-string! this ::password :event %)
      :label    "Password"
      :error    false})
    (ui-form-input
     {:value    confirm-password
      :onChange #(fm/set-string! this ::confirm-password :event %)
      :label    "Confirm Password"
      :error    false})
    (u.inputs/ui-primary-button
     {:content "Submit"}
     {:onClick
      (fn []
        (let [data {:user/username username
                    :user/password password}]
          (comp/transact! this [`(mu.session/register ~data)])))})))

(def ui-registration-form (comp/factory RegistrationForm))
