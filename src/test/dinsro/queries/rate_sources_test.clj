(ns dinsro.queries.rate-sources-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [datahike.api :as d]
   [datahike.config :as dc]
   [dinsro.config :as config]
   [dinsro.db :as db]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.specs :as ds]
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
      (d/transact db/*conn* m.currencies/schema)
      (d/transact db/*conn* m.rate-sources/schema)
      (f))))

(deftest create-record-test
  (let [params (ds/gen-key ::m.rate-sources/params)
        id (q.rate-sources/create-record params)
        item (q.rate-sources/read-record id)]
    (is (= (::m.rate-sources/name params) (::m.rate-sources/name item))
        "rate sources match")))

(deftest read-record-test-not-found
  (let [id (ds/gen-key :db/id)
        response (q.rate-sources/read-record id)]
    (is (nil? response)
        "Should return nil")))

(deftest read-record-test-found
  (let [item (mocks/mock-rate-source)
        response (q.rate-sources/read-record (:db/id item))]
    (is (= item response)
        "Return the matching item")))

(deftest index-records-no-records
  (is (= [] (q.rate-sources/index-records))))

(deftest index-records-with-records
  (let [item (mocks/mock-rate-source)
        response (q.rate-sources/index-records)]
    (is (= [item] response))))
