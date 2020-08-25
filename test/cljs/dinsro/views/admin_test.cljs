(ns dinsro.views.admin-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.views.admin :as v.admin]))

(defcard-rg title
  [:div
   [:h1.title "Admin View"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.views_test"}
      "Views"]]]

   [:ul.box]])

(defcard-rg load-buttons
  [v.admin/load-buttons])

(defcard-rg users-section
  [v.admin/users-section])

(defcard-rg page
  [v.admin/page])
