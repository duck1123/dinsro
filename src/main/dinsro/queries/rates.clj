(ns dinsro.queries.rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
   [dinsro.components.streams :as streams]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [manifold.stream :as ms]
   [taoensso.timbre :as log]
   [tick.alpha.api :as tick]))

(def record-limit 75)

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.rates/id ?id]])

(def find-id-by-eid-query
  '[:find  ?id
    :in    $ ?eid
    :where [?eid ::m.rates/id ?id]])

(>defn find-eid-by-id
  [id]
  [::m.rates/id => :db/id]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-eid-by-id-query id))))

(>defn find-id-by-eid
  [eid]
  [:db/id => ::m.rates/id]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-id-by-eid-query eid))))

(>defn find-ids-by-rate-source
  [rate-source-id]
  [::m.rate-sources/id => (s/coll-of ::m.rates/id)]
  (let [db (c.crux/main-db)
        query '{:find [?rate-id]
                :in [?rate-source-id]
                :where [[?rate-id ::m.rates/source ?rate-source-id]]}]
    (map first (crux/q db query rate-source-id))))

(>defn prepare-record
  [params]
  [::m.rates/params => ::m.rates/params]
  (update params ::m.rates/rate double))

(>defn create-record
  [params]
  [::m.rates/params => :db/id]
  (let [node            (c.crux/main-node)
        id              (utils/uuid)
        prepared-params (-> (prepare-record params)
                            (assoc ::m.rates/id id)
                            (assoc :crux.db/id id)
                            (update ::m.rates/date tick/inst))]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/put prepared-params]]))
    (comment (ms/put! streams/message-source [::create-record [:dinsro.events.rates/add-record id]]))
    id))

(>defn read-record
  [id]
  [:db/id => (? ::m.rates/item)]
  (let [db     (c.crux/main-db)
        record (crux/pull db '[*] id)]
    (when (get record ::m.rates/rate)
      (-> record
          (update ::m.rates/date tick/instant)
          (dissoc :db/id)))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (let [db (c.crux/main-db)]
    (map first (crux/q db '[:find ?e :where [?e ::m.rates/rate _]]))))

(>defn index-records
  []
  [=> (s/coll-of ::m.rates/item)]
  (map read-record (index-ids)))

(>defn index-records-by-currency
  [currency-id]
  [:db/id => ::m.rates/rate-feed]
  (let [db    (c.crux/main-db)
        query '{:find  [?date ?rate]
                :in    [?currency-eid]
                :where [[?currency-eid ::m.currencies/id ?currency]
                        [?e ::m.rates/currency ?currency]
                        [?e ::m.rates/rate ?rate]
                        [?e ::m.rates/date ?date]]}]
    (->> (crux/q db query currency-id)
         (sort-by first)
         (reverse)
         (take record-limit)
         (map (fn [[date rate]] [(.getTime date) rate])))))

(>defn delete-record
  [id]
  [:db/id => nil?]
  (let [node (c.crux/main-node)]
    (crux/submit-tx node [[:db/retractEntity id]]))
  nil)

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))

(comment

  (index-records)

  nil)
