(ns dinsro.queries.rates-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]))

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest create-record-success
  (let [currency    (mocks/mock-currency)
        currency-id (::m.currencies/id currency)
        params      (ds/gen-key ::m.rates/params)
        params      (assoc params ::m.rates/currency currency-id)
        id          (q.rates/create-record params)
        item        (q.rates/read-record id)]
    (assertions
     (double (::m.rates/rate params)) => (::m.rates/rate item))))

(deftest read-record-not-found
  (let [id (ds/gen-key uuid?)]
    (assertions
     (q.rates/read-record id) => nil)))

(deftest read-record-found
  (let [{::m.rates/keys [id]
         :as            item} (mocks/mock-rate)]
    (assertions
     (q.rates/read-record id) => item)))
