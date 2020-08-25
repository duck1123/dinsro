(ns dinsro.spec.views-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [taoensso.timbre :as timbre]
   ;; [dinsro.spec.views.show-currency-test]

   ))

(defcard-rg title
  [:div
   [:h1.title "View Specs"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.views_test"}
      "Views"]]]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.views.show_currencies_test"}
      "Show Currency View"]]]])
