(ns dinsro.queries.currencies-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.check :as _]
   [fulcro-spec.core :refer [assertions]]
   [taoensso.timbre :as log]))

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record-valid
  (let [params (ds/gen-key ::m.currencies/params)]
    (assertions
     (q.currencies/create-record params) =check=> (_/is?* uuid?))))

(deftest read-record-success
  (let [{::m.currencies/keys [id] :as item} (mocks/mock-currency)]
    (assertions
     (q.currencies/read-record (q.currencies/find-eid-by-id id)) => item)))

(deftest read-record-not-found
  (let [id (ds/gen-key :xt/id)]
    (assertions
     "should return nil"
     (q.currencies/read-record id) => nil)))

(deftest index-records-success
  (q.currencies/delete-all)
  (assertions
   (q.currencies/index-records) => []))

(deftest index-records-with-records
  (let [currency (mocks/mock-currency)]
    (assertions
     (q.currencies/index-records) => [currency])))

(deftest delete-record-success
  (let [record                     (mocks/mock-currency)
        {::m.currencies/keys [id]} record
        eid                        (q.currencies/find-eid-by-id id)]
    (assertions
     (q.currencies/read-record eid) => record
     (q.currencies/delete-record eid) => nil
     (q.currencies/read-record eid) => nil)))
