(ns dinsro.ui.registration
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.header.ui-header :refer [ui-header]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.forms.registration :as u.f.registration]))

(def index-page-key :registration)
(def parent-router :root)

(defsc IndexPage
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
    (ui-container {:textAlign "center"}
      (ui-header {:textAlign "center" :attached "top"} "Register")
      (ui-segment {:textAlign "center" :attached "top"}
        (u.f.registration/ui-registration-form form)))
    (dom/div {}
      (dom/p {} "Registrations are not enabled"))))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Registration"
   ::m.navlinks/parent-key    parent-router
   ::m.navlinks/router        parent-router
   ::m.navlinks/required-role :user})
