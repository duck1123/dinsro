(ns dinsro.components.currency-rates-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.currency-rates :as c.currency-rates]
   [dinsro.store.mock :refer [mock-store]]))

(cards/header "Currency Rates Components" [])

(let [currency-id 7
      rates [[1 1]
             [2 2]
             [3 4]]
      store (mock-store)]
  (defcard-rg c.currency-rates/section
    (fn []
      [error-boundary
       (c.currency-rates/section store currency-id rates)])))
