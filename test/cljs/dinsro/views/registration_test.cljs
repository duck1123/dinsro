(ns dinsro.views.registration-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1 "Registration Page"]
   [:ul
    [:a {:href "devcards.html#!/dinsro.components.forms.registration_test"}
     "Registration Component"]]])
