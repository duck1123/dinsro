(ns dinsro.spec.actions-test
  (:require
   ;; dinsro.spec.actions.accounts-test
   ;; dinsro.spec.actions.categories-test
   ;; dinsro.spec.actions.rate-sources-test
   ;; dinsro.spec.actions.rates-test
   [devcards.core :refer-macros [defcard-rg]]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1 "Actions Specs"]
   [:ul
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.actions.accounts_test"}
      "Account Actions Specs"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.actions.authentication_test"}
      "Authentication Actions Specs"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.actions.categories_test"}
      "Category Actions Specs"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.actions.rate_sources_test"}
      "Rate Source Actions Specs"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.actions.rates_test"}
      "Rate Actions Specs"]]]])
