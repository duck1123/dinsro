(ns dinsro.views.show-currency-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.spec.views.show-currency :as s.v.show-currency]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.show-currency :as v.show-currency]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Show Currency View"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.views_test"}
      "Views"]]]

   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.currencies_test"}
      "Currency Spec"]]

    [:li
     [:a {:href "devcards.html#!/dinsro.spec.views.show_currency_test"}
      "Show Currency View Spec"]]

    [:li
     [:a {:href "devcards.html#!/dinsro.components.show_currency_test"}
      "Show Currency Components"]]]])

(let [item (ds/gen-key ::s.currencies/item)]

  (defcard item-card item)

  (defcard init-page-cofx
    (ds/gen-key ::s.v.show-currency/init-page-cofx))

  (defcard init-page-event
    (ds/gen-key ::s.v.show-currency/init-page-event))

  (defcard init-page-response
    (ds/gen-key ::s.v.show-currency/init-page-response))

  (defcard view-map
    (ds/gen-key ::s.v.show-currency/view-map))

  (defcard-rg v.show-currency/page-loaded
    [v.show-currency/page-loaded item])

  (let [store (mock-store)
        match {:path-params {:id "1"}}]
    (deftest page
      (is (vector? (v.show-currency/page store match))))

    (defcard-rg page-card
      [v.show-currency/page store match])))
