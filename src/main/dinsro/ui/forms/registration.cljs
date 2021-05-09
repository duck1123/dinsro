(ns dinsro.ui.forms.registration
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.mutations.session :as mu.session]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as log]))

(defsc RegistrationForm
  [this {::keys [confirm-password id password]}]
  {:ident         (fn [] [:component/id ::form])
   :initial-state {::confirm-password "hunter2"
                   ::id               "admin"
                   ::password         "hunter2"}
   :query         [::confirm-password
                   ::id
                   ::password]}
  (dom/div {}
    (u.inputs/ui-text-input
     {:label "Username" :value id}
     {:onChange #(fm/set-string! this ::id :event %)})
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
        (log/info "clicked")
        (let [data {:user/username id
                    :user/password password}]
          (comp/transact! this [(mu.session/register data)])))})))

(def ui-registration-form (comp/factory RegistrationForm))
