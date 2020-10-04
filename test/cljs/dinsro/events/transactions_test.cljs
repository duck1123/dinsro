(ns dinsro.events.transactions-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.cards :as cards]
   [dinsro.event_utils :as eu]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.spec.events.transactions :as s.e.transactions]
   [dinsro.spec :as ds]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.events.transactions-test
 "Transaction Events"
 [#{:events} #{:transactions} #{:transactions :events}])

(defcard item
  (ds/gen-key ::e.transactions/item))

(defcard ::e.transactions/do-fetch-index-cofx
  (ds/gen-key ::s.e.transactions/do-fetch-index-cofx))

(defcard do-fetch-index-response
  "7"
  )

(defcard do-fetch-index-response-card
  (ds/gen-key ::e.transactions/do-fetch-index-response))

(let [store (mock-store)]
  (deftest do-fetch-index
    (let [cofx {}
          event [{:foo "bar"}]
          ns-sym 'dinsro.events.transactions
          path-selector [:api-show-index]
          response (eu/do-fetch-index
                    ns-sym
                    path-selector
                    store
                    cofx
                    event)]
      (is (= {} response)))))
