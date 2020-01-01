(ns dinsro.model.categories
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec :as ds]
            [dinsro.spec.categories :as s.categories]
            [taoensso.timbre :as timbre]))

(defn create-record
  [params]
  (let [response (d/transact db/*conn* {:tx-data [(assoc params :db/id "record-id")]})]
    (get-in response [:tempids "record-id"])))

(s/fdef create-record
  :args (s/cat :params ::s.categories/params)
  :ret ::ds/id)

(defn read-record
  [id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::s.categories/name)
      record)))

(s/fdef read-record
  :args (s/cat :id ::ds/id)
  :ret  (s/nilable ::s.categories/item))

(defn index-ids
  []
  (map first (d/q '[:find ?e :where [?e ::s.categories/name _]] @db/*conn*)))

(s/fdef index-ids
  :args (s/cat)
  :ret (s/coll-of ::ds/id))

(defn index-records
  []
  (d/pull-many @db/*conn* '[*] (index-ids)))

(s/fdef index-records
  :args (s/cat)
  :ret (s/coll-of ::s.categories/item))

(defn delete-record
  [id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(s/fdef delete-record
  :args (s/cat :id ::ds/id)
  :ret nil?)

(defn delete-all
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(s/fdef delete-all
  :args (s/cat)
  :ret nil?)
