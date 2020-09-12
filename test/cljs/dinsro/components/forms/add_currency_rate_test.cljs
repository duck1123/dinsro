(ns dinsro.components.forms.add-currency-rate-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.add-currency-rate :as c.f.add-currency-rate]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-currency-rate :as e.f.add-currency-rate]
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]))

(cards/header 'dinsro.components.forms.add-currency-rate-test
 "Add Currency Rate Form Components" [])

(defn test-store
  []
  (let [store (doto (mock-store)
                e.currencies/init-handlers!
                e.debug/init-handlers!
                e.f.add-currency-rate/init-handlers!
                e.f.create-rate/init-handlers!)]
    store))

(let [currency-id 1]
  (comment (defcard currency-id currency-id))

  (let [store (test-store)]
    (st/dispatch store [::e.f.add-currency-rate/set-shown? true])

    (defcard-rg form
      (fn []
        [error-boundary
         [c.f.add-currency-rate/form store currency-id]]))

    (deftest form-test
      (is (vector? (c.f.add-currency-rate/form store currency-id))))))
