(ns dinsro.components.currency-rates-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.components.currency-rates :as c.currency-rates]))

(defcard-rg c.currency-rates/section
  "**Currency Rates Section**"
  (fn []
    [:div.box
     [c.currency-rates/section 7  [
                                   [ 1 1 ]
                                   [ 2 2 ]
                                   [ 3 4 ]
                                   ]]])
  {})
