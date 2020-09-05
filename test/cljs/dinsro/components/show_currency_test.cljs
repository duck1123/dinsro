(ns dinsro.components.show-currency-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.show-currency :as c.show-currency]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.translations :refer [tr]]))

(cards/header "Show Currency Components" [])

(let [item (ds/gen-key ::s.currencies/item)]
  (defcard item item)

  (defcard-rg show-currency
    (fn []
      [error-boundary
       [c.show-currency/show-currency item]])))
