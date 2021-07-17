(ns dinsro.queries.transactions-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]
   [taoensso.timbre :as log]))

(def schemata
  [m.users/schema
   m.accounts/schema
   m.currencies/schema
   m.rates/schema
   m.transactions/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record-success
  (let [account    (mocks/mock-account)
        account-id (::m.accounts/id account)
        params     (ds/gen-key ::m.transactions/required-params)
        params     (assoc params ::m.transactions/account {::m.accounts/id account-id})
        id         (q.transactions/create-record params)
        record     (q.transactions/read-record id)]
    (assertions
     (double (::m.transactions/value params)) => (::m.transactions/value record))))

(deftest read-record-success
  (let [{::m.transactions/keys [id] :as item} (mocks/mock-transaction)]
    (assertions
     (q.transactions/read-record (q.transactions/find-eid-by-id id)) => item)))

(deftest read-record-not-found
  (let [id (ds/gen-key :db/id)]
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
