(ns dinsro.model.currencies
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre])
  (:import datahike.db.TxReport))

(defn-spec index-ids (s/coll-of ::ds/id)
  []
  (map first (d/q '[:find ?e :where [?e ::s.currencies/name _]] @db/*conn*)))

(defn-spec index-records (s/coll-of ::s.currencies/item)
  []
  (->> (index-ids)
       (d/pull-many @db/*conn* '[::s.currencies/name :db/id])))

(defn-spec create-record ::ds/id
  [params ::s.currencies/params]
  (let [params (assoc params :db/id "currency-id")]
    (let [response (d/transact db/*conn* {:tx-data [params]})]
      (get-in response [:tempids "currency-id"]))))

(defn-spec read-record (s/nilable ::s.currencies/item)
  [id ::ds/id]
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

(defn-spec mock-record ::s.currencies/item
  []
  (let [params (gen/generate (s/gen ::s.currencies/params))
        id (create-record params)]
    (read-record id)))

(comment
  (index-ids)
  (delete-all)
  (index-records)
  (mock-record)
  )
