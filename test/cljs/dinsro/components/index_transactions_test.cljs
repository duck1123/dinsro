(ns dinsro.components.index-transactions-test
  (:require
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.index-transactions :as c.index-transactions]
   [dinsro.spec :as ds]
   [dinsro.spec.transactions :as s.transactions]
   [dinsro.translations :refer [tr]]))

(cards/header "Index Transaction Components" [])

(let [item (ds/gen-key ::s.transactions/item)]
  (defcard item item)

  (defcard-rg c.index-transactions/row-line
    (fn []
      [error-boundary
       [c.index-transactions/row-line item]])))

(let [items (ds/gen-key (s/coll-of ::s.transactions/item))]
  (defcard items items)

  (defcard-rg c.index-transactions/index-transactions
    (fn []
      [error-boundary
       [c.index-transactions/index-transactions items]])))
