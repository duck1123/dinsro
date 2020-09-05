(ns dinsro.spec.actions.rate-sources-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.spec :as ds]
   [dinsro.spec.actions.rate-sources :as s.a.rate-sources]))

(defcard-rg title
  [:div
   [:h1.title "Rate Source Actions Specs"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.actions_test"}
      "Action Specs"]]]])

(defcard create-params-valid
  (ds/gen-key ::s.a.rate-sources/create-params-valid))

(defcard create-request-valid
  (ds/gen-key ::s.a.rate-sources/create-request-valid))
