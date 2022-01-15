(ns dinsro.queries.rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.components.streams :as streams]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.specs]
   [manifold.stream :as ms]
   [taoensso.timbre :as log]
   [tick.alpha.api :as tick]
   [xtdb.api :as xt]))

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
  [::m.rates/id => :xt/id]
  (let [db (c.xtdb/main-db)]
    (ffirst (xt/q db find-eid-by-id-query id))))

(>defn find-id-by-eid
  [eid]
  [:xt/id => ::m.rates/id]
  (let [db (c.xtdb/main-db)]
    (ffirst (xt/q db find-id-by-eid-query eid))))

(>defn find-ids-by-rate-source
  [rate-source-id]
  [::m.rate-sources/id => (s/coll-of ::m.rates/id)]
  (let [db (c.xtdb/main-db)
        query '{:find [?rate-id]
                :in [?rate-source-id]
                :where [[?rate-id ::m.rates/source ?rate-source-id]]}]
    (map first (xt/q db query rate-source-id))))

(>defn find-by-currency
  [currency-id]
  [::m.currencies/id => (s/coll-of ::m.rates/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?rate-id]
                :in    [?currency-id]
                :where [[?rate-id ::m.rates/source ?rate-source-id]
                        [?rate-source-id ::m.rate-sources/currency ?currency-id]]}]
    (map first (xt/q db query currency-id))))

(>defn find-top-by-currency
  [currency-id]
  [::m.currencies/id => (? ::m.rates/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find     [?rate-id ?date]
                :in       [?currency-id]
                :where    [[?rate-id ::m.rates/source ?rate-source-id]
                           [?rate-id ::m.rates/date ?date]
                           [?rate-source-id ::m.rate-sources/currency ?currency-id]]
                :order-by [[?date :desc]]
                :limit    1}]
    (ffirst (xt/q db query currency-id))))

(>defn find-top-by-rate-source
  [source-id]
  [::m.rate-sources/id => (? ::m.rates/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find     [?rate-id ?date]
                :in       [?source-id]
                :where    [[?rate-id ::m.rates/source ?source-id]
                           [?rate-id ::m.rates/date ?date]]
                :order-by [[?date :desc]]
                :limit    1}]
    (ffirst (xt/q db query source-id))))

(>defn prepare-record
  [params]
  [::m.rates/params => ::m.rates/params]
  (update params ::m.rates/rate double))

(>defn create-record
  [params]
  [::m.rates/params => :xt/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> (prepare-record params)
                            (assoc ::m.rates/id id)
                            (assoc :xt/id id)
                            (update ::m.rates/date tick/inst))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (comment (ms/put! streams/message-source [::create-record [:dinsro.events.rates/add-record id]]))
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.rates/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.rates/rate)
      (-> record
          (update ::m.rates/date tick/instant)
          (dissoc :xt/id)))))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (let [db (c.xtdb/main-db)]
    (map first (xt/q db '[:find ?e :where [?e ::m.rates/rate _]]))))

(>defn index-records
  []
  [=> (s/coll-of ::m.rates/item)]
  (map read-record (index-ids)))

(>defn index-records-by-currency
  [currency-id]
  [:xt/id => ::m.rates/rate-feed]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?date ?rate]
                :in    [?currency-eid]
                :where [[?currency-eid ::m.currencies/id ?currency]
                        [?e ::m.rates/currency ?currency]
                        [?e ::m.rates/rate ?rate]
                        [?e ::m.rates/date ?date]]}]
    (->> (xt/q db query currency-id)
         (sort-by first)
         (reverse)
         (take record-limit)
         (map (fn [[date rate]] [(.getTime date) rate])))))

(>defn delete-record
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/submit-tx node [[:db/retractEntity id]]))
  nil)

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))

(comment

  (index-records)

  nil)
