(ns dinsro.views.show-currency-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.spec.views.show-currency :as s.v.show-currency]
   [dinsro.views.show-currency :as v.show-currency]
   [taoensso.timbre :as timbre]))

(let [item (ds/gen-key ::s.currencies/item)]

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

  (deftest page
    (is (vector? (v.show-currency/page {:path-params {:id "1"}}))))

  (defcard-rg page-card
    [v.show-currency/page]))
