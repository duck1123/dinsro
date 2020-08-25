(ns dinsro.events.forms-test
  (:require
   [devcards.core :as dc :refer-macros [defcard-rg]]
   ;; [dinsro.events.forms.add-user-account-test]
   ;; [dinsro.events.forms.create-account-test]
   ;; [dinsro.events.forms.registration-test]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Form Events"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms_test"}
      "Form Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.events_test"}
      "Events"]]]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.events.forms.add_user_account_test"}
      "Add User Account Forms Events"]]

    [:li
     [:a {:href "devcards.html#!/dinsro.events.forms.registration_test"}
      "Registration Form Events"]]]])
