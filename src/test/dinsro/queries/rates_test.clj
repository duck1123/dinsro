(ns dinsro.queries.rates-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [taoensso.timbre :as timbre]
   [tick.core :as tick]))

(def schemata [m.currencies/schema
               m.rates/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record-test
  (let [params (ds/gen-key ::m.rates/params)
        id     (q.rates/create-record params)
        item   (q.rates/read-record id)]
    (is (= (double (::m.rates/rate params)) (::m.rates/rate item))
        "rates match")))

(deftest read-record-test-not-found
  (let [id       (ds/gen-key pos-int?)
        response (q.rates/read-record id)]
    (is (nil? response)
        "Should return nil")))

(deftest read-record-test-found
  (let [item     (mocks/mock-rate)
        id       (:db/id item)
        response (q.rates/read-record id)]
    (is (= item response)
        "Return the matching item")))

(deftest index-records-no-recorde
  (is (= [] (q.rates/index-records))))

(deftest index-records-with-records
  (let [item     (mocks/mock-rate)
        response (q.rates/index-records)]
    (is (= [item] response))))

(deftest index-records-by-currency-with-records
  (let [currency    (mocks/mock-currency)
        currency-id (:db/id currency)
        params      (ds/gen-key ::m.rates/params)
        params      (assoc params ::m.rates/currency {:db/id currency-id})
        rate-id     (q.rates/create-record params)
        rate        (q.rates/read-record rate-id)
        response    (q.rates/index-records-by-currency currency-id)
        date        (.getTime (tick/inst (::m.rates/date rate)))]
    (is (= date (nth (first response) 0)))
    (is (= (::m.rates/rate rate) (nth (first response) 1)))))
