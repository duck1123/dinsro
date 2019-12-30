(ns dinsro.model.categories
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec :as ds]
            [dinsro.spec.categories :as s.categories]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(defn-spec prepare-account ::s.categories/params
  [params ::s.categories/params]
  params)

(defn-spec create-record ::ds/id
  [params ::s.categories/params]
  (let [response (d/transact db/*conn* {:tx-data [(assoc params :db/id "record-id")]})]
    (get-in response [:tempids "record-id"])))

(defn-spec read-record (s/nilable ::s.categories/item)
  [id ::ds/id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::s.categories/name)
      record)))

(defn-spec index-ids (s/coll-of ::ds/id)
  []
  (map first (d/q '[:find ?e :where [?e ::s.categories/name _]] @db/*conn*)))

(defn-spec index-records (s/* ::s.categories/item)
  []
  (d/pull-many @db/*conn* '[*] (index-ids)))

(defn-spec delete-record any?
  [id ::ds/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]}))

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))
