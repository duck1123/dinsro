(ns dinsro.ui.forms.registration
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [taoensso.timbre :as timbre]))

(defsc RegistrationForm
  [this {:keys [confirm-password email name password]}]
  {:query [:confirm-password :email :name :password]
   :initial-state {:confirm-password "" :email "" :name "" :password ""}}
  (let [on-name-change (fn [evt _] (fm/set-string! this :name :event evt))
        on-email-change (fn [evt _] (fm/set-string! this :email :event evt))
        on-password-change (fn [evt _] (fm/set-string! this :password :event evt))
        on-confirm-password-change (fn [evt _] (fm/set-string! this :confirm-password :event evt))]
    (bulma/box
     (dom/div "Registration form")
     (dom/form
      (ui-form-input {:label "Name" :value name :onChange on-name-change})
      (ui-form-input {:label "Email" :value email :onChange on-email-change})
      (ui-form-input {:label "Password" :value password :onChange on-password-change})
      (ui-form-input {:label "Confirm Password" :value confirm-password :onChange on-confirm-password-change})
      (ui-button {:content "Submit"})))))

(def ui-registration-form (comp/factory RegistrationForm))
