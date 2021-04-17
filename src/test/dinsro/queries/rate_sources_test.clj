(ns dinsro.queries.rate-sources-test
  (:require
   [clojure.test :refer [use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions behavior specification]]))

(def schemata [m.currencies/schema
               m.rate-sources/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(specification "create-record"
  (behavior "success"
    (let [currency    (mocks/mock-currency)
          currency-id (::m.currencies/id currency)
          params      (ds/gen-key ::m.rate-sources/required-params)
          params      (assoc-in params [::m.rate-sources/currency ::m.currencies/id] currency-id)
          id          (q.rate-sources/create-record params)
          item        (q.rate-sources/read-record id)]
      (assertions
       (::m.rate-sources/name params) => (::m.rate-sources/name item)))))

(specification "read-record"
  (behavior "not found"
    (let [id (ds/gen-key :db/id)]
      (assertions
       (q.rate-sources/read-record id) => nil)))
  (behavior "found"
    (let [item (mocks/mock-rate-source)
          id   (::m.rate-sources/id item)]
      (assertions
       (q.rate-sources/read-record (q.rate-sources/find-eid-by-id id)) => item))))

(specification "index-records"
  (behavior "no records"
    (assertions
     (q.rate-sources/index-records) => []))
  (behavior "with records"
    (let [item (mocks/mock-rate-source)]
      (assertions
       (q.rate-sources/index-records) => [item]))))
