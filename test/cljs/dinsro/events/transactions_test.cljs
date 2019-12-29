(ns dinsro.events.transactions-test
  (:require [cljs.test :refer-macros [is]]
            [devcards.core :refer-macros [defcard deftest]]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.spec.events.transactions :as s.e.transactions]
            [dinsro.specs :as ds]
            [taoensso.timbre :as timbre]))

(defcard ::s.transactions/item
  (ds/gen-key ::s.transactions/item))

(defcard ::s.e.transactions/do-fetch-index-cofx
  (ds/gen-key ::s.e.transactions/do-fetch-index-cofx))

(defcard ::s.e.transactions/do-fetch-index-event
  (ds/gen-key ::s.e.transactions/do-fetch-index-event))

(defcard ::s.e.transactions/do-fetch-index-response
  (ds/gen-key ::s.e.transactions/do-fetch-index-response))

(deftest do-fetch-index
  (let [cofx {}
        event [{:foo "bar"}]]
    (is (= 1 (e.transactions/do-fetch-index cofx event)))))

(defcard ::s.e.transactions/do-submit-cofx
  (ds/gen-key ::s.e.transactions/do-submit-cofx))

(defcard ::s.e.transactions/do-submit-event
  (ds/gen-key ::s.e.transactions/do-submit-event))

(defcard ::s.e.transactions/do-submit-response
  (ds/gen-key ::s.e.transactions/do-submit-response))
