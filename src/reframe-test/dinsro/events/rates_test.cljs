(ns dinsro.events.rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.events.rates :as e.rates]
   [dinsro.specs :as ds]
   [dinsro.specs.events.rates :as s.e.rates]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(let [cofx {}
      store (mock-store)]
  (let [event (ds/gen-key ::s.e.rates/add-record-event-without-response)]
    (deftest add-record-no-response
      (let [id (first event)]
        (is (= {:dispatch [::e.rates/do-fetch-record id [::e.rates/add-record id]]}
               (e.rates/add-record store cofx event))))))
  (let [event (ds/gen-key ::s.e.rates/add-record-event-with-response)]
    (deftest add-record-response
      (is (= {} (e.rates/add-record store cofx event))))))
