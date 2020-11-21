(ns dinsro.queries.transactions-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [datahike.api :as d]
   [datahike.config :refer [uri->config]]
   [dinsro.components.config :as config]
   [dinsro.db :as db]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.specs :as ds]
   [mount.core :as mount]
   [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/config #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* m.users/schema)
      (d/transact db/*conn* m.currencies/schema)
      (d/transact db/*conn* m.rates/schema)
      (d/transact db/*conn* m.transactions/schema)
      (f))))

(deftest create-record-test
  (let [params (ds/gen-key ::m.transactions/params)
        id (q.transactions/create-record params)
        record (q.transactions/read-record id)]

    (is (= (double (::m.transactions/value params))
           (::m.transactions/value record))
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
  (let [params (ds/gen-key ::m.transactions/params)]
    (q.transactions/create-record params)
    (is (not= [params] (q.transactions/index-records)))))

(deftest delete-record
  (let [item (mocks/mock-transaction)
        id (:db/id item)]
    (is (not (nil? (q.transactions/read-record id))))
    (let [response (q.transactions/delete-record id)]
      (is (nil? response))
      (is (nil? (q.transactions/read-record id))))))
