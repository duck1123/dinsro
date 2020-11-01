(ns dinsro.model.rates-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [datahike.api :as d]
   [datahike.config :as dc]
   [dinsro.config :as config]
   [dinsro.db :as db]
   [dinsro.mocks :as mocks]
   [dinsro.model.rates :as m.rates]
   [dinsro.specs :as ds]
   [dinsro.specs.currencies :as s.currencies]
   [dinsro.specs.rates :as s.rates]
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
      (d/transact db/*conn* s.currencies/schema)
      (d/transact db/*conn* s.rates/schema)
      (f))))

(deftest create-record-test
  (let [params (ds/gen-key ::s.rates/params)
        id (m.rates/create-record params)
        item (m.rates/read-record id)]
    (is (= (double (::s.rates/rate params)) (::s.rates/rate item))
        "rates match")))

(deftest read-record-test-not-found
  (let [response (m.rates/read-record (ds/gen-key pos-int?))]
    (is (nil? response)
        "Should return nil")))

(deftest read-record-test-found
  (let [item (mocks/mock-rate)
        response (m.rates/read-record (:db/id item))]
    (is (= item response)
        "Return the matching item")))

(deftest index-records-no-recorde
  (is (= [] (m.rates/index-records))))

(deftest index-records-with-records
  (let [item (mocks/mock-rate)
        response (m.rates/index-records)]
    (is (= [item] response))))

(deftest index-records-by-currency-with-records
  (let [currency (mocks/mock-currency)
        currency-id (:db/id currency)
        params (ds/gen-key ::s.rates/params)
        params (assoc params ::s.rates/currency {:db/id currency-id})
        rate-id (m.rates/create-record params)
        rate (m.rates/read-record rate-id)
        response (m.rates/index-records-by-currency currency-id)
        date (.getTime (tick/inst (::s.rates/date rate)))]
    (is (= date (nth (first response) 0)))
    (is (= (::s.rates/rate rate) (nth (first response) 1)))))
