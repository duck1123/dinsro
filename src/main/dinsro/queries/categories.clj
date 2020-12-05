(ns dinsro.queries.categories
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.categories :as m.categories]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]))

(>defn create-record
  [params]
  [::m.categories/params => ::ds/id]
  (let [response (d/transact db/*conn* {:tx-data [(assoc params :db/id "record-id")]})]
    (get-in response [:tempids "record-id"])))

(>defn read-record
  [id]
  [::ds/id => (? ::m.categories/item)]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.categories/name)
      record)))

(>defn index-ids
  []
  [=> (s/coll-of ::ds/id)]
  (map first (d/q '[:find ?e :where [?e ::m.categories/name _]] @db/*conn*)))

(>defn index-records
  []
  [=> (s/coll-of ::m.categories/item)]
  (d/pull-many @db/*conn* '[*] (index-ids)))

(>defn delete-record
  [id]
  [::ds/id => nil?]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
