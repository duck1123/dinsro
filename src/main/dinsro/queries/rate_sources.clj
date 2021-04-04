(ns dinsro.queries.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.specs]
   [taoensso.timbre :as timbre]))

(>defn create-record
  [params]
  [::m.rate-sources/params => :db/id]
  (let [response (d/transact db/*conn* {:tx-data [(assoc params :db/id "rate-source-id")]})]
    (get-in response [:tempids "rate-source-id"])))

(>defn read-record
  [id]
  [:db/id => (? ::m.rate-sources/item)]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.rate-sources/name)
      record)))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (map first (d/q '[:find ?e :where [?e ::m.rate-sources/name _]] @db/*conn*)))

(>defn index-records
  []
  [=> (s/coll-of ::m.rate-sources/item)]
  (d/pull-many @db/*conn* '[*] (index-ids)))

(>defn delete-record
  [id]
  [:db/id => any?]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]}))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))

(comment
  (index-ids)
  (index-records)
  (delete-all))
