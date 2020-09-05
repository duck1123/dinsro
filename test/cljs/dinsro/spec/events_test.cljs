(ns dinsro.spec.events-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Event Specs"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.events_test"}
      "Events"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.specs_test"}
      "Specs"]]]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms.add_user_account_test"}
      "Add User Account Forms Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.event.forms.add_user_account_test"}
      "Add User Account Forms Event Specs"]]]])
