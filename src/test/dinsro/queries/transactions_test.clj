(ns dinsro.queries.transactions-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]))

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record-success
  (let [account    (mocks/mock-account)
        account-id (::m.accounts/id account)
        params     (ds/gen-key ::m.transactions/required-params)
        params     (assoc params ::m.transactions/account account-id)
        id         (q.transactions/create-record params)
        record     (q.transactions/read-record id)]
    (assertions
     (double (::m.transactions/value params)) => (::m.transactions/value record))))

(deftest read-record-success
  (let [{::m.transactions/keys [id] :as item} (mocks/mock-transaction)]
    (assertions
     (q.transactions/read-record (q.transactions/find-eid-by-id id)) => item)))

(deftest read-record-not-found
  (let [id (ds/gen-key :xt/id)]
    (assertions
     (q.transactions/read-record id) => nil)))

(deftest index-records-success
  (q.transactions/delete-all)
  (assertions
   (q.transactions/index-records) => []))

(deftest index-records-with-records
  (let [transaction (mocks/mock-transaction)]
    (assertions
     (q.transactions/index-records) => [transaction])))

(deftest delete-record-success
  (let [{::m.transactions/keys [id] :as item} (mocks/mock-transaction)
        eid                                   (q.transactions/find-eid-by-id id)]
    (assertions
     "the record should exist to start"
     (q.transactions/read-record eid) => item

     "should return nil"
     (q.transactions/delete-record eid) => nil

     "the record shouldn't exist after"
     (q.transactions/read-record eid) => nil)))

(comment

  (mocks/mock-account)
  (q.transactions/index-records)
  (q.transactions/index-ids)

  (q.transactions/delete-record   (first (q.transactions/index-ids)))

  nil)
