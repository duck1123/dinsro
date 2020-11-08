(ns dinsro.queries.currencies-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [datahike.api :as d]
   [datahike.config :refer [uri->config]]
   [dinsro.config :as config]
   [dinsro.db :as db]
   [dinsro.mocks :as mocks]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.specs :as ds]
   [dinsro.specs.currencies :as s.currencies]
   [dinsro.specs.users :as s.users]
   [mount.core :as mount]
   [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.users/schema)
      (d/transact db/*conn* s.currencies/schema)
      (f))))

(deftest create-record-success
  (let [params (ds/gen-key ::s.currencies/params)
        response (q.currencies/create-record params)]
    (is (not (nil? response)))))

(deftest read-record-success
  (let [item (mocks/mock-currency)
        id (:db/id item)
        response (q.currencies/read-record id)]
    (is (= item response))))

(deftest read-record-not-found
  (let [id (ds/gen-key ::ds/id)
        response (q.currencies/read-record id)]
    (is (nil? response))))

(deftest index-records-success
  (q.currencies/delete-all)
  (is (= [] (q.currencies/index-records))))

(deftest index-records-with-records
  (is (not= nil (mocks/mock-user)))
  (let [params (ds/gen-key ::s.currencies/params)]
    (q.currencies/create-record params)
    (is (not= [params] (q.currencies/index-records)))))

(deftest delete-record
  (let [currency (mocks/mock-currency)
        id (:db/id currency)]
    (is (not (nil? (q.currencies/read-record id))))
    (let [response (q.currencies/delete-record id)]
      (is (nil? response))
      (is (nil? (q.currencies/read-record id))))))
