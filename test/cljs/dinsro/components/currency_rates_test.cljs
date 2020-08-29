(ns dinsro.components.currency-rates-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.currency-rates :as c.currency-rates]))

(let [currency-id 7
      rates [[1 1]
             [2 2]
             [3 4]]]
  (defcard-rg c.currency-rates/section
    "**Currency Rates Section**"
    (fn []
      [error-boundary
       [c.currency-rates/section currency-id rates]])))
