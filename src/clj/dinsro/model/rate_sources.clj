(ns dinsro.model.rate-sources
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec.rate-sources :as s.rate-sources]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(defn-spec prepare-account ::s.rate-sources/params
  [params ::s.rate-sources/params]
  params)

(defn-spec create-record ::ds/id
  [params ::s.rate-sources/params]
  (let [response (d/transact db/*conn* {:tx-data [(assoc params :db/id "rate-source-id")]})]
    (get-in response [:tempids "rate-source-id"])))

(defn-spec read-record (s/nilable ::s.rate-sources/item)
  [id ::ds/id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::s.rate-sources/name)
      record)))

(defn-spec index-ids (s/coll-of ::ds/id)
  []
  (map first (d/q '[:find ?e :where [?e ::s.rate-sources/name _]] @db/*conn*)))

(defn-spec index-records (s/* ::s.rate-sources/item)
  []
  (d/pull-many @db/*conn* '[*] (index-ids)))

(defn-spec delete-record any?
  [id ::ds/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]}))

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(comment
  (index-ids)
  (index-records)
  (delete-all)
  )
