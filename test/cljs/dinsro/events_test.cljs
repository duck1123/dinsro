(ns dinsro.events-test
  (:require
   dinsro.events.accounts-test
   dinsro.events.rates-test
   dinsro.events.transactions-test
   [devcards.core :as dc :refer-macros [defcard-rg]]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Events"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.events.forms_test"}
      "Form Events"]]]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.events.accounts_test"}
      "Account Events"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.events.rates_test"}
      "Rate Events"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.events.transactions_test"}
      "Transactions Events"]]]])
