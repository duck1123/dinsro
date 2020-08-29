(ns dinsro.components.show-currency-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.show-currency :as c.show-currency]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.translations :refer [tr]]))

(defcard-rg title
  [:div
   [:h1.title "Show Currency Components"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]]

   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.currencies_test"}
      "Currency Spec"]]]])

(let [item (ds/gen-key ::s.currencies/item)]
  (defcard item item)

  (defcard-rg show-currency
    (fn []
      [error-boundary
       [c.show-currency/show-currency item]])))
