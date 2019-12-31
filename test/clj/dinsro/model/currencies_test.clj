(ns dinsro.model.currencies-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [datahike.api :as d]
            [datahike.config :refer [uri->config]]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.mocks :as mocks]
            [dinsro.model.currencies :as m.currencies]
            [dinsro.specs :as ds]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.users :as s.users]
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
        response (m.currencies/create-record params)]
    (is (not (nil? response)))))

(deftest read-record-success
  (let [item (mocks/mock-currency)
        id (:db/id item)
        response (m.currencies/read-record id)]
    (is (= item response))))

(deftest read-record-not-found
  (let [id (ds/gen-key ::ds/id)
        response (m.currencies/read-record id)]
    (is (nil? response))))

(deftest index-records-success
  (m.currencies/delete-all)
  (is (= [] (m.currencies/index-records))))

(deftest index-records-with-records
  (is (not= nil (mocks/mock-user)))
  (let [params (ds/gen-key ::s.currencies/params)
        id (m.currencies/create-record params)]
    (is (not= [params] (m.currencies/index-records)))))

(deftest delete-record
  (let [currency (mocks/mock-currency)
        id (:db/id currency)]
    (is (not (nil? (m.currencies/read-record id))))
    (let [response (m.currencies/delete-record id)]
      (is (nil? response))
      (is (nil? (m.currencies/read-record id))))))
