(ns dinsro.ui.show-currency-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.debug :as e.debug]
   [dinsro.specs :as ds]
   [dinsro.specs.currencies :as s.currencies]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.boundary]
   [dinsro.ui.show-currency :as u.show-currency]))

(let [item (ds/gen-key ::s.currencies/item)
      store (doto (mock-store)
              e.debug/init-handlers!)]
  (defcard-rg show-currency
    (st/dispatch store [::e.debug/set-shown? true])
    [u.show-currency/show-currency store item])

  (deftest show-currency-test
    (is (vector? (u.show-currency/show-currency store item)))))
