(ns dinsro.ui.forms.registration
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc RegistrationForm
  [this {:keys [confirm-password email name password]}]
  {:initial-state {:confirm-password ""
                   :email            ""
                   :name             ""
                   :password         ""}
   :query [:confirm-password
           :email
           :name
           :password]}
  (bulma/box
   (dom/form
    (u.inputs/ui-text-input
     {:label "Name" :value name}
     {:onChange (fn [evt _] (fm/set-string! this :name :event evt))})
    (u.inputs/ui-text-input
     {:label "Email" :value email}
     {:onChange (fn [evt _] (fm/set-string! this :email :event evt))})
    (u.inputs/ui-text-input
     {:label "Password" :value password}
     {:onChange (fn [evt _] (fm/set-string! this :password :event evt))})
    (u.inputs/ui-text-input
     {:label "Confirm Password" :value confirm-password}
     {:onChange (fn [evt _] (fm/set-string! this :confirm-password :event evt))})
    (ui-button {:content "Submit"}))))

(def ui-registration-form (comp/factory RegistrationForm))
