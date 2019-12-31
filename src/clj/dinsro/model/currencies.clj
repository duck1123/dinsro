(ns dinsro.model.currencies
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(defn index-ids
  []
  (map first (d/q '[:find ?e :where [?e ::s.currencies/name _]] @db/*conn*)))

(s/fdef index-ids
  :ret (s/coll-of ::ds/id))

(defn-spec index-records
  []
  (->> (index-ids)
       (d/pull-many @db/*conn* '[::s.currencies/name :db/id])))

(s/fdef index-records
  :ret (s/coll-of ::s.currencies/item))

(defn-spec create-record :db/id
  [params ::s.currencies/params]
  (let [params (assoc params :db/id "currency-id")
        response (d/transact db/*conn* {:tx-data [params]})]
    (get-in response [:tempids "currency-id"])))

(defn-spec read-record (s/nilable ::s.currencies/item)
  [id :db/id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::s.currencies/name)
      record)))

(defn-spec delete-record nil?
  [id ::ds/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))
