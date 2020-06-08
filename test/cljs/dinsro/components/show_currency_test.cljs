(ns dinsro.components.show-currency-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.show-currency :as c.show-currency]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.translations :refer [tr]]))

(let [item (ds/gen-key ::s.currencies/item)]
  (defcard item item)

  (defcard-rg show-currency
    [error-boundary
     [c.show-currency/show-currency item]]))
