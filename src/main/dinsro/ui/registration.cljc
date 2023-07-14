(ns dinsro.ui.registration
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.forms.registration :as u.f.registration]))

(def index-page-key :registration)

(defsc Page
  [_this {:ui/keys [allow-registration? form]}]
  {:ident          (fn [_] [::m.navlinks/id index-page-key])
   :initial-state  {:ui/allow-registration? true
                    :ui/form                {}
                    ::m.navlinks/id         index-page-key}
   ::m.navlinks/id :registration
   :query          [:ui/allow-registration?
                    {:ui/form (comp/get-query u.f.registration/RegistrationForm)}
                    ::m.navlinks/id]
   :route-segment  ["register"]}
  (if allow-registration?
    (dom/div :.ui.container.center.aligned
      (dom/h4 :.ui.center.aligned.top.attached.header "Register")
      (dom/div :.ui.center.aligned.attached.segment
        (u.f.registration/ui-registration-form form)))
    (dom/div {}
      (dom/p {} "Registrations are not enabled"))))

(m.navlinks/defroute   :registration
  {::m.navlinks/control       ::Page
   ::m.navlinks/label         "Registration"
   ::m.navlinks/parent-key    :root
   ::m.navlinks/router        :root
   ::m.navlinks/required-role :user})
