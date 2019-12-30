(ns dinsro.model.transactions
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec :as ds]
            [dinsro.spec.transactions :as s.transactions]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]
            [tick.alpha.api :as tick]))

(defn-spec create-record ::ds/id
  [params ::s.transactions/params]
  (let [tempid (d/tempid "transaction-id")
        prepared-params (-> params
                            (assoc  :db/id tempid)
                            (update ::s.transactions/date tick/inst))
        response (d/transact db/*conn* {:tx-data [prepared-params]})]
    (get-in response [:tempids tempid])))

(defn-spec read-record (s/nilable ::s.transactions/item)
  [id ::ds/id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::s.transactions/value)
      (update record ::s.transactions/date tick/instant))))

(defn-spec index-ids (s/coll-of ::ds/id)
  []
  (map first (d/q '[:find ?e :where [?e ::s.transactions/value _]] @db/*conn*)))

(defn-spec index-records (s/coll-of ::s.transactions/item)
  []
  (->> (index-ids)
       (d/pull-many @db/*conn* '[*])
       (map #(update % ::s.transactions/date tick/instant))))

(defn-spec delete-record nil?
  [id ::ds/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))
