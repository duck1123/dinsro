(ns dinsro.components.show-currency-test
  (:require [devcards.core :refer-macros [defcard defcard-rg]]
            [dinsro.components.show-currency :as c.show-currency]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]))

(let [item (ds/gen-key ::s.currencies/item)]
  (defcard item item)
  (defcard-rg show-currency
    [c.show-currency/show-currency item]))
