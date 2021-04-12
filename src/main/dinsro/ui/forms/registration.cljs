(ns dinsro.ui.forms.registration
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.mutations.session :as mu.session]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc RegistrationForm
  [this {::keys [confirm-password email name password]}]
  {:ident         (fn [] [:component/id ::form])
   :initial-state {::confirm-password "hunter2"
                   ::email            "bob@example.com"
                   ::name             "bob"
                   ::password         "hunter2"}
   :query         [::confirm-password
                   ::email
                   ::name
                   ::password]}
  (dom/div
   (u.inputs/ui-text-input
    {:label "Name" :value name}
    {:onChange #(fm/set-string! this ::name :event %)})
   (u.inputs/ui-text-input
    {:label "Email" :value email}
    {:onChange #(fm/set-string! this ::email :event %)})
   (u.inputs/ui-password-input
    {:label "Password" :value password}
    {:onChange #(fm/set-string! this ::password :event %)})
   (u.inputs/ui-password-input
    {:label "Confirm Password" :value confirm-password}
    {:onChange #(fm/set-string! this ::confirm-password :event %)})
   (u.inputs/ui-primary-button
    {:content "Submit"}
    {:onClick
     (fn []
       (timbre/info "clicked")
       (let [data {:user/name     name
                   :user/email    email
                   :user/password password}]
         (comp/transact! this [(mu.session/register data)])))})))

(def ui-registration-form (comp/factory RegistrationForm))
