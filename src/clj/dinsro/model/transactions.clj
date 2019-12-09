(ns dinsro.model.transactions
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            #_[dinsro.spec.rates :as s.rates]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(defn-spec prepare-record ::s.transactions/params
  [params ::s.transactions/params]
  params
  #_(let [rate (::s.rates/rate params)]
    (update params ::s.rates/rate double)))

(defn-spec create-record ::ds/id
  [params ::s.transactions/params]
  (let [tempid (d/tempid "transaction-id")
        prepared-params (assoc (prepare-record params) :db/id tempid)
        response (d/transact db/*conn* {:tx-data [prepared-params]})]
    (get-in response [:tempids tempid])))
