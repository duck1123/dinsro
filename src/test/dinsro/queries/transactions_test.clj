(ns dinsro.queries.transactions-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [taoensso.timbre :as timbre]))

(def schemata
  [m.users/schema
   m.accounts/schema
   m.currencies/schema
   m.rates/schema
   m.transactions/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record-test
  (let [params (ds/gen-key ::m.transactions/params)
        id     (q.transactions/create-record params)
        record (q.transactions/read-record id)]

    (is (= (double (::m.transactions/value params))
           (::m.transactions/value record))
        "values match")))

(deftest read-record-success
  (let [item     (mocks/mock-transaction)
        id       (:db/id item)
        response (q.transactions/read-record id)]
    (is (= item response))))

(deftest read-record-not-found
  (let [id       (ds/gen-key :db/id)
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
        id   (:db/id item)]
    (is (not (nil? (q.transactions/read-record id))))
    (let [response (q.transactions/delete-record id)]
      (is (nil? response))
      (is (nil? (q.transactions/read-record id))))))
