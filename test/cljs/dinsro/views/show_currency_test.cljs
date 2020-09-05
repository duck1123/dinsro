(ns dinsro.views.show-currency-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.spec.views.show-currency :as s.v.show-currency]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.show-currency :as v.show-currency]
   [taoensso.timbre :as timbre]))

(cards/header "Show Currency View" [])

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
    (fn []
      [error-boundary
       [v.show-currency/page-loaded item]]))

  (let [store (mock-store)
        match {:path-params {:id "1"}}]
    (deftest page
      (is (vector? (v.show-currency/page store match))))

    (defcard-rg page-card
      (fn []
        [error-boundary
         [v.show-currency/page store match]]))))
