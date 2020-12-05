(ns dinsro.queries.currencies
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]))

(>defn index-ids
  []
  [=> (s/coll-of ::ds/id)]
  (map first (d/q '[:find ?e :where [?e ::m.currencies/name _]] @db/*conn*)))

(>defn index-records
  []
  [=> (s/coll-of ::m.currencies/item)]
  (d/pull-many @db/*conn* '[::m.currencies/name :db/id] (index-ids)))

(>defn create-record
  [params]
  [::m.currencies/params => ::ds/id]
  (let [params (assoc params :db/id "currency-id")
        response (d/transact db/*conn* {:tx-data [params]})]
    (get-in response [:tempids "currency-id"])))

(>defn read-record
  [id]
  [::ds/id => (? ::m.currencies/item)]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.currencies/name)
      record)))

(>defn delete-record
  [id]
  [::ds/id => nil?]
  (do
    (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
