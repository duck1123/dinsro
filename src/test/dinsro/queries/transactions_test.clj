(ns dinsro.queries.transactions-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [datahike.api :as d]
   [datahike.config :refer [uri->config]]
   [dinsro.config :as config]
   [dinsro.db :as db]
   [dinsro.mocks :as mocks]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.specs :as ds]
   [dinsro.specs.currencies :as s.currencies]
   [dinsro.specs.rates :as s.rates]
   [dinsro.specs.transactions :as s.transactions]
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
      (d/transact db/*conn* s.rates/schema)
      (d/transact db/*conn* s.transactions/schema)
      (f))))

(deftest create-record-test
  (let [params (ds/gen-key ::s.transactions/params)
        id (q.transactions/create-record params)
        record (q.transactions/read-record id)]

    (is (= (double (::s.transactions/value params))
           (::s.transactions/value record))
        "values match")))

(deftest read-record-success
  (let [item (mocks/mock-transaction)
        id (:db/id item)
        response (q.transactions/read-record id)]
    (is (= item response))))

(deftest read-record-not-found
  (let [id (ds/gen-key ::ds/id)
        response (q.transactions/read-record id)]
    (is (nil? response))))

(deftest index-records-success
  (q.transactions/delete-all)
  (is (= [] (q.transactions/index-records))))

(deftest index-records-with-records
  (is (not= nil (mocks/mock-user)))
  (let [params (ds/gen-key ::s.transactions/params)]
    (q.transactions/create-record params)
    (is (not= [params] (q.transactions/index-records)))))

(deftest delete-record
  (let [item (mocks/mock-transaction)
        id (:db/id item)]
    (is (not (nil? (q.transactions/read-record id))))
    (let [response (q.transactions/delete-record id)]
      (is (nil? response))
      (is (nil? (q.transactions/read-record id))))))