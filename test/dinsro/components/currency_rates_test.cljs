(ns dinsro.components.currency-rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.currency-rates :as c.currency-rates]
   [dinsro.events.forms.add-currency-rate :as e.f.add-currency-rate]
   [dinsro.store.mock :refer [mock-store]]))

(let [currency-id 7
      rates [[1 1]
             [2 2]
             [3 4]]
      store (doto (mock-store)
              e.f.add-currency-rate/init-handlers!)]

  (defcard-rg c.currency-rates/section
    (fn []
      [error-boundary
       [c.currency-rates/section store currency-id rates]]))

  (deftest section-test
    (is (vector? (c.currency-rates/section store currency-id rates)))))
