(ns dinsro.model.rates-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.mocks :as mocks]
            [dinsro.model.rates :as m.rates]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [mount.core :as mount]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (datahike.config/uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.currencies/schema)
      (d/transact db/*conn* s.rates/schema)
      (f))))

(deftest create-record-test
  (testing "success"
    (let [params (gen/generate (s/gen ::s.rates/params))
          id (m.rates/create-record params)
          item (m.rates/read-record id)]
      (is (= (double (::s.rates/rate params)) (::s.rates/rate item)) "rates match"))))

(deftest read-record-test-not-found
  (testing "not found"
    (let [response (m.rates/read-record (gen/generate (s/gen pos-int?)))]
      (is (nil? response) "Should return nil"))))

(deftest read-record-test-found
  (testing "when found"
    (let [item (mocks/mock-rate)
          response (m.rates/read-record (:db/id item))]
      (is (= item response) "Return the matching item"))))

(deftest index-records-no-recorde
  (testing "no records"
    (is (= [] (m.rates/index-records)))))

(deftest index-records-with-records
  (testing "with record"
    (let [item (mocks/mock-rate)
          response (m.rates/index-records)]
      (is (= [item] response)))))
