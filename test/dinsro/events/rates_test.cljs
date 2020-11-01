(ns dinsro.events.rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.events.rates :as e.rates]
   [dinsro.spec :as ds]
   [dinsro.spec.events.rates :as s.e.rates]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(defcard "**Add record**")

(let [cofx {}
      store (mock-store)]
  (defcard add-record-cofx cofx)
  (let [event (ds/gen-key ::s.e.rates/add-record-event-without-response)]
    (defcard add-record-event-without-response event)
    (deftest add-record-no-response
      (let [id (first event)]
        (is (= {:dispatch [::e.rates/do-fetch-record id [::e.rates/add-record id]]}
               (e.rates/add-record store cofx event))))))
  (let [event (ds/gen-key ::s.e.rates/add-record-event-with-response)]
    (defcard add-record-event-with-response event)
    (deftest add-record-response
      (is (= {} (e.rates/add-record store cofx event))))))
