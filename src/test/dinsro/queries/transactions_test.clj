(ns dinsro.queries.transactions-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]))

;; [[../../../main/dinsro/queries/transactions.clj]]

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest read-record-success
  (let [{::m.transactions/keys [id] :as item} (mocks/mock-transaction)]
    (assertions
     (q.transactions/read-record id) => item)))

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

(deftest delete!-success
  (let [{::m.transactions/keys [id] :as item} (mocks/mock-transaction)]
    (assertions
     "the record should exist to start"
     (q.transactions/read-record id) => item

     "should return nil"
     (q.transactions/delete! id) => nil

     "the record shouldn't exist after"
     (q.transactions/read-record id) => nil)))

(comment

  (mocks/mock-account)
  (q.transactions/index-records)
  (q.transactions/index-ids)

  (q.transactions/delete! (first (q.transactions/index-ids)))

  nil)
