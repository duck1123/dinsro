(ns dinsro.queries.currencies-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.check :as _]
   [fulcro-spec.core :refer [assertions]]
   [taoensso.timbre :as log]))

(def schemata
  [m.users/schema
   m.currencies/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record-valid
  (let [params (ds/gen-key ::m.currencies/params)]
    (assertions
     (q.currencies/create-record params) =check=> (_/is?* number?))))

(deftest read-record-success
  (let [{::m.currencies/keys [id] :as item} (mocks/mock-currency)]
    (assertions
     (q.currencies/read-record (q.currencies/find-eid-by-id id)) => item)))

(deftest read-record-not-found
  (let [id (ds/gen-key :db/id)]
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
  (let [{::m.currencies/keys [id] :as record} (mocks/mock-currency)
        eid                                   (q.currencies/find-eid-by-id id)]
    (assertions
     (q.currencies/read-record eid) => record
     (q.currencies/delete-record eid) => nil
     (q.currencies/read-record eid) => nil)))
