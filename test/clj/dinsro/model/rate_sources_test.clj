(ns dinsro.model.rate-sources-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [datahike.api :as d]
            [datahike.config :as dc]
            [dinsro.config :as config]
            [dinsro.db :as db]
            [dinsro.mocks :as mocks]
            [dinsro.model.rate-sources :as m.rate-sources]
            [dinsro.spec :as ds]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rate-sources :as s.rate-sources]
            [mount.core :as mount]))

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
      (d/transact db/*conn* s.rate-sources/schema)
      (f))))

(deftest create-record-test
  (let [params (ds/gen-key ::s.rate-sources/params)
        id (m.rate-sources/create-record params)
        item (m.rate-sources/read-record id)]
    (is (= (::s.rate-sources/name params) (::s.rate-sources/name item))
        "rate sources match")))

(deftest read-record-test-not-found
  (let [id (ds/gen-key :db/id)
        response (m.rate-sources/read-record id)]
    (is (nil? response)
        "Should return nil")))

(deftest read-record-test-found
  (let [item (mocks/mock-rate-source)
        response (m.rate-sources/read-record (:db/id item))]
    (is (= item response)
        "Return the matching item")))

(deftest index-records-no-records
  (is (= [] (m.rate-sources/index-records))))

(deftest index-records-with-records
  (let [item (mocks/mock-rate-source)
        response (m.rate-sources/index-records)]
    (is (= [item] response))))
