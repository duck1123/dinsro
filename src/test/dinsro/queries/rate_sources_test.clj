(ns dinsro.queries.rate-sources-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]))

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record-success
  (let [currency    (mocks/mock-currency)
        currency-id (::m.currencies/id currency)
        params      (ds/gen-key ::m.rate-sources/required-params)
        params      (assoc params ::m.rate-sources/currency currency-id)
        id          (q.rate-sources/create-record params)
        item        (q.rate-sources/read-record id)]
    (assertions
     (::m.rate-sources/name params) => (::m.rate-sources/name item))))

(deftest read-record-not-found
  (let [id (ds/gen-key ::m.rate-sources/id)]
    (assertions
     (q.rate-sources/read-record id) => nil)))

(deftest read-record-found
  (let [item (mocks/mock-rate-source)
        id   (::m.rate-sources/id item)]
    (assertions
     (q.rate-sources/read-record id) => item)))

(deftest index-records-no-records
  (assertions
   (q.rate-sources/index-records) => []))

(deftest index-records-with-records
  (let [item (mocks/mock-rate-source)]
    (assertions
     (q.rate-sources/index-records) => [item])))
