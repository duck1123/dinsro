(ns dinsro.views.admin-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.store.mock :refer [mock-store]]
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
  (fn []
    [error-boundary
     [v.admin/load-buttons]]))

(defcard-rg users-section
  (fn []
    [error-boundary
     [v.admin/users-section]]))

(let [store (mock-store)
      match nil]
  (defcard-rg page
    (fn []
      [error-boundary
       [v.admin/page store match]])))
