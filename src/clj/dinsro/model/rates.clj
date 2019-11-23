(ns dinsro.model.rates
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec.rates :as s.rates]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(defn-spec prepare-record ::s.rates/params
  [params ::s.rates/params]
  params)

(defn-spec create-record :db/id
  [params ::s.rates/params]
  (let [tempid (d/tempid "rate-id")
        prepared-params (assoc (prepare-record params) :db/id tempid)
        response (d/transact db/*conn* {:tx-data [prepared-params]})]
    (get-in response [:tempids tempid])))

(defn-spec read-record (s/nilable ::s.rates/item)
  [id :db/id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::s.rates/value)
      record)))

(defn-spec index-ids (s/coll-of :db/id)
  []
  (map first (d/q '[:find ?e :where [?e ::s.rates/value _]] @db/*conn*)))

(defn-spec index-records (s/coll-of ::s.rates/item :kind vector?)
  []
  (->> (index-ids)
       (d/pull-many @db/*conn* '[*])))

(defn-spec delete-record nil?
  [id :db/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(defn-spec mock-record ::s.rates/item
  []
  (read-record (create-record (gen/generate (s/gen ::s.rates/prepared-params)))))

(comment
  (gen/generate (s/gen ::s.rates/prepared-params))
  (delete-all)
  (index-records)
  (mock-record)

  (read-record 59)

  (d/entity @db/*conn* 59)
  )
