(ns dinsro.queries.rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.rates :as m.rates]
   [dinsro.specs]
   [dinsro.streams :as streams]
   [dinsro.utils :as utils]
   [manifold.stream :as ms]
   [taoensso.timbre :as timbre]
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
  (ffirst (d/q find-eid-by-id-query @db/*conn* id)))

(>defn find-id-by-eid
  [eid]
  [:db/id => ::m.rates/id]
  (ffirst (d/q find-id-by-eid-query @db/*conn* eid)))

(>defn prepare-record
  [params]
  [::m.rates/params => ::m.rates/params]
  (update params ::m.rates/rate double))

(>defn create-record
  [params]
  [::m.rates/params => :db/id]
  (let [tempid          (d/tempid "rate-id")
        prepared-params (-> (prepare-record params)
                            (assoc ::m.rates/id (utils/uuid))
                            (assoc :db/id tempid)
                            (update ::m.rates/date tick/inst))
        response        (d/transact db/*conn* {:tx-data [prepared-params]})
        id              (get-in response [:tempids tempid])]
    (ms/put! streams/message-source [::create-record [:dinsro.events.rates/add-record id]])
    id))

(>defn read-record
  [id]
  [:db/id => (? ::m.rates/item)]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.rates/rate)
      (update record ::m.rates/date tick/instant))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (map first (d/q '[:find ?e :where [?e ::m.rates/rate _]] @db/*conn*)))

(>defn index-records
  []
  [=> (s/coll-of ::m.rates/item)]
  (->> (index-ids)
       (d/pull-many @db/*conn* '[*])
       (sort-by ::m.rates/date)
       (reverse)
       (take record-limit)
       (map #(update % ::m.rates/date tick/instant))))

(>defn index-records-by-currency
  [currency-id]
  [:db/id => ::m.rates/rate-feed]
  (->> (d/q {:query '[:find ?date ?rate
                      :in $ ?currency
                      :where
                      [?e ::m.rates/currency ?currency]
                      [?e ::m.rates/rate ?rate]
                      [?e ::m.rates/date ?date]]
             :args  [@db/*conn* currency-id]})
       (sort-by first)
       (reverse)
       (take record-limit)
       (map (fn [[date rate]] [(.getTime date) rate]))))

(>defn delete-record
  [id]
  [:db/id => nil?]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
