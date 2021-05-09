(ns dinsro.queries.rates-test
  (:require
   [clojure.test :refer [use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions behavior specification]]
   [taoensso.timbre :as log]
   [tick.core :as tick]))

(def schemata [m.currencies/schema
               m.rates/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(specification "create-record"
  (behavior "success"
    (let [currency (mocks/mock-currency)
          id       (::m.currencies/id currency)
          params   (ds/gen-key ::m.rates/params)
          params   (assoc params ::m.rates/currency #::m.currencies{:id id})
          id       (q.rates/create-record params)
          item     (q.rates/read-record id)]
      (assertions
       (double (::m.rates/rate params)) => (::m.rates/rate item)))))

(specification "read-record"
  (behavior "not found"
    (let [id (ds/gen-key pos-int?)]
      (assertions
       (q.rates/read-record id) => nil)))
  (behavior "found"
    (let [{::m.rates/keys [id]
           :as            item} (mocks/mock-rate)
          eid                   (q.rates/find-eid-by-id id)]
      (assertions
       (q.rates/read-record eid) => item))))

(specification "index-records"
  (behavior "no records"
    (assertions
     (q.rates/index-records) => []))
  (behavior "with records"
    (let [item (mocks/mock-rate)]
      (assertions
       (q.rates/index-records) => [item]))))

(specification "index-records-by-currency"
  (behavior "with records"
    (let [currency    (mocks/mock-currency)
          currency-id (::m.currencies/id currency)
          params      (ds/gen-key ::m.rates/params)
          params      (assoc params ::m.rates/currency {::m.currencies/id currency-id})
          rate-id     (q.rates/create-record params)
          rate        (q.rates/read-record rate-id)
          response    (q.rates/index-records-by-currency (q.currencies/find-eid-by-id currency-id))
          date        (.getTime (tick/inst (::m.rates/date rate)))]
      (assertions
       (nth (first response) 0) => date
       (nth (first response) 1) => (::m.rates/rate rate)))))
