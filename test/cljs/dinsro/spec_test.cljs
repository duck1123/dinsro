(ns dinsro.spec-test
  (:require
   dinsro.spec.accounts-test
   dinsro.spec.currencies-test
   dinsro.spec.rates-test
   dinsro.spec.transactions-test
   ;; [dinsro.spec.users-test]
   [devcards.core :as dc :refer-macros [defcard-rg]]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Specs"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms_test"}
      "Form Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.events_test"}
      "Events"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.views_test"}
      "Views"]]]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.accounts_test"}
      "Account Specs"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.currencies_test"}
      "Currency Specs"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.rates_test"}
      "Rate Specs"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.transactions_test"}
      "Transaction Specs"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.users_test"}
      "User Specs"]]]])
