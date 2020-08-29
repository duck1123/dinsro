(ns dinsro.components.status-test
  (:require
   [day8.re-frame.http-fx]
   [devcards.core :refer-macros [defcard-rg]]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Status Components"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]]

   [:ul.box]])
