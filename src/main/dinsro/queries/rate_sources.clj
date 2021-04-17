(ns dinsro.queries.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as timbre]))

(def attributes-list
  '[:db/id
    ::m.rate-sources/id
    ::m.rate-sources/name])
(def record-limit 1000)

(def find-dbid-by-id-query
  '[:find ?dbid
    :where [?dbid ::m.rate-sources/id _]])

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.rate-sources/id ?id]])

(def find-id-by-eid-query
  '[:find  ?id
    :in    $ ?eid
    :where [?eid ::m.rate-sources/id ?id]])

(>defn find-eid-by-id
  [id]
  [::m.rate-sources/id => :db/id]
  (ffirst (d/q find-eid-by-id-query @db/*conn* id)))

(>defn find-id-by-eid
  [eid]
  [:db/id => ::m.rate-sources/id]
  (ffirst (d/q find-id-by-eid-query @db/*conn* eid)))

;; (def find-dbid-by-id-query
;;   '[:find ?dbid
;;     :where [?dbid ::m.rate-sources/id _]])

;; (>defn find-dbid-by-id
;;   [id]
;;   [::m.rate-sources/id => (? :db/id)]
;;   (d/q find-dbid-by-id-query @db/*conn* id))

(>defn create-record
  [params]
  [::m.rate-sources/params => :db/id]
  (let [params   (assoc params ::m.rate-sources/id (utils/uuid))
        params   (assoc params :db/id "rate-source-id")
        response (d/transact db/*conn* {:tx-data [params]})]
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

(defn index-records-by-currency
  [currency-id]
  (->> (d/q {:query '[:find
                      ?id
                      ?currency-id
                      :keys db/id name
                      :in $ ?currency-id
                      :where
                      [?id ::m.rate-sources/currency ?currency-id]]
             :args  [@db/*conn* currency-id]})
       (map :db/id)
       (map read-record)
       (take record-limit)))

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
  (index-records-by-currency 408231720)
  (delete-all))
