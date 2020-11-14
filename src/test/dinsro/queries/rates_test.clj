(ns dinsro.queries.rates-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [datahike.api :as d]
   [datahike.config :as dc]
   [dinsro.config :as config]
   [dinsro.db :as db]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs :as ds]
   [mount.core :as mount]
   [taoensso.timbre :as timbre]
   [tick.core :as tick]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (dc/uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* m.currencies/schema)
      (d/transact db/*conn* m.rates/schema)
      (f))))

(deftest create-record-test
  (let [params (ds/gen-key ::m.rates/params)
        id (q.rates/create-record params)
        item (q.rates/read-record id)]
    (is (= (double (::m.rates/rate params)) (::m.rates/rate item))
        "rates match")))

(deftest read-record-test-not-found
  (let [response (q.rates/read-record (ds/gen-key pos-int?))]
    (is (nil? response)
        "Should return nil")))

(deftest read-record-test-found
  (let [item (mocks/mock-rate)
        response (q.rates/read-record (:db/id item))]
    (is (= item response)
        "Return the matching item")))

(deftest index-records-no-recorde
  (is (= [] (q.rates/index-records))))

(deftest index-records-with-records
  (let [item (mocks/mock-rate)
        response (q.rates/index-records)]
    (is (= [item] response))))

(deftest index-records-by-currency-with-records
  (let [currency (mocks/mock-currency)
        currency-id (:db/id currency)
        params (ds/gen-key ::m.rates/params)
        params (assoc params ::m.rates/currency {:db/id currency-id})
        rate-id (q.rates/create-record params)
        rate (q.rates/read-record rate-id)
        response (q.rates/index-records-by-currency currency-id)
        date (.getTime (tick/inst (::m.rates/date rate)))]
    (is (= date (nth (first response) 0)))
    (is (= (::m.rates/rate rate) (nth (first response) 1)))))
