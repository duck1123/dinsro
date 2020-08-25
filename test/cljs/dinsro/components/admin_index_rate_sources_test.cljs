(ns dinsro.components.admin-index-rate-sources-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.components.admin-index-rate-sources :as c.admin-index-rate-sources]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.translations :refer [tr]]))

(defcard-rg title
  [:div
   [:h1.title "Admin Index Rate Source Components"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]]])

(defcard-rg c.admin-index-rate-sources/form
  "**Admin Index Rate Sources**"
  (fn []
    [error-boundary
     [c.admin-index-rate-sources/section]])
  {})
