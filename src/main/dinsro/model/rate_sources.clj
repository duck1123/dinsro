(ns dinsro.model.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.specs :as ds]
   [dinsro.specs.rate-sources :as s.rate-sources]
   [taoensso.timbre :as timbre]))

(defn create-record
  [params]
  (let [response (d/transact db/*conn* {:tx-data [(assoc params :db/id "rate-source-id")]})]
    (get-in response [:tempids "rate-source-id"])))

(s/fdef create-record
  :args (s/cat :params ::s.rate-sources/params)
  :ret :db/id)

(defn read-record
  [id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::s.rate-sources/name)
      record)))

(s/fdef read-record
  :args (s/cat :id :db/id)
  :ret (s/nilable ::s.rate-sources/item))

(defn index-ids
  []
  (map first (d/q '[:find ?e :where [?e ::s.rate-sources/name _]] @db/*conn*)))

(s/fdef index-ids
  :args (s/cat)
  :ret (s/coll-of ::ds/id))

(defn index-records
  []
  (d/pull-many @db/*conn* '[*] (index-ids)))

(s/fdef index-records
  :args (s/cat)
  :ret (s/coll-of ::s.rate-sources/item))

(defn delete-record
  [id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]}))

(s/fdef delete-record
  :args (s/cat :id :db/id)
  :ret any?)

(defn delete-all
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(s/fdef delete-all
  :args (s/cat)
  :ret nil?)

(comment
  (index-ids)
  (index-records)
  (delete-all)
  )
