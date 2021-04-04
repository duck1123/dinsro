(ns dinsro.queries.currencies-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [taoensso.timbre :as timbre]))

(def schemata
  [m.users/schema
   m.currencies/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record-success
  (let [params (ds/gen-key ::m.currencies/params)
        response (q.currencies/create-record params)]
    (is (not (nil? response)))))

(deftest read-record-success
  (let [item (mocks/mock-currency)
        id (:db/id item)
        response (q.currencies/read-record id)]
    (is (= item response))))

(deftest read-record-not-found
  (let [id (ds/gen-key :db/id)
        response (q.currencies/read-record id)]
    (is (nil? response))))

(deftest index-records-success
  (q.currencies/delete-all)
  (is (= [] (q.currencies/index-records))))

(deftest index-records-with-records
  (is (not= nil (mocks/mock-user)))
  (let [params (ds/gen-key ::m.currencies/params)]
    (q.currencies/create-record params)
    (is (not= [params] (q.currencies/index-records)))))

(deftest delete-record
  (let [currency (mocks/mock-currency)
        id (:db/id currency)]
    (is (not (nil? (q.currencies/read-record id))))
    (let [response (q.currencies/delete-record id)]
      (is (nil? response))
      (is (nil? (q.currencies/read-record id))))))
