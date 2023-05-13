(ns dinsro.queries.rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.streams :as streams]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [manifold.stream :as ms]
   [tick.alpha.api :as tick]
   [xtdb.api :as xt]))

(def record-limit 75)

(>defn find-by-rate-source
  [rate-source-id]
  [::m.rate-sources/id => (s/coll-of ::m.rates/id)]
  (c.xtdb/query-values
   '{:find [?rate-id]
     :in [[?rate-source-id]]
     :where [[?rate-id ::m.rates/source ?rate-source-id]]}
   [rate-source-id]))

(>defn find-by-currency
  [currency-id]
  [::m.currencies/id => (s/coll-of ::m.rates/id)]
  (c.xtdb/query-values
   '{:find  [?rate-id]
     :in    [[?currency-id]]
     :where [[?rate-id ::m.rates/source ?rate-source-id]
             [?rate-source-id ::m.rate-sources/currency ?currency-id]]}
   [currency-id]))

(>defn find-top-by-currency
  [currency-id]
  [::m.currencies/id => (? ::m.rates/id)]
  (c.xtdb/query-value
   '{:find     [?rate-id ?date]
     :in       [[?currency-id]]
     :where    [[?rate-id ::m.rates/source ?rate-source-id]
                [?rate-id ::m.rates/date ?date]
                [?rate-source-id ::m.rate-sources/currency ?currency-id]]
     :order-by [[?date :desc]]
     :limit    1}
   [currency-id]))

(>defn find-top-by-rate-source
  [source-id]
  [::m.rate-sources/id => (? ::m.rates/id)]
  (c.xtdb/query-value
   '{:find     [?rate-id ?date]
     :in       [[?source-id]]
     :where    [[?rate-id ::m.rates/source ?source-id]
                [?rate-id ::m.rates/date ?date]]
     :order-by [[?date :desc]]
     :limit    1}
   [source-id]))

(>defn prepare-record
  [params]
  [::m.rates/params => ::m.rates/params]
  (update params ::m.rates/rate double))

(>defn create-record
  [params]
  [::m.rates/params => :xt/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> (prepare-record params)
                            (assoc ::m.rates/id id)
                            (assoc :xt/id id)
                            (update ::m.rates/date tick/inst))]
    (log/trace :create-record/prepared {:prepared-params prepared-params})
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/trace :create-record/finished {:id id})
    (comment (ms/put! streams/message-source [::create-record [:dinsro.events.rates/add-record id]]))
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.rates/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.rates/rate)
      (-> record
          (update ::m.rates/date tick/instant)
          (dissoc :xt/id)))))

(defn get-index-query
  [query-params]
  (let [{rate-source-id ::m.rate-sources/id} query-params]
    {:find  ['?rate-id]
     :in    [['?rate-source-id]]
     :where (->> [['?rate-id ::m.rates/id '_]]
                 (concat (when rate-source-id
                           [['?rate-id ::m.rates/source '?rate-source-id]]))
                 (filter identity)
                 (into []))}))

(defn get-index-params
  [query-params]
  (let [{rate-source-id ::m.rate-sources/id} query-params]
    [rate-source-id]))

(>defn count-ids
  ([]
   [=> number?]
   (count-ids {}))
  ([query-params]
   [map? => number?]
   (do
     (log/debug :count-ids/starting {:query-params query-params})
     (let [base-params  (get-index-query query-params)
           limit-params {:find ['(count ?rate-id)]}
           params       (get-index-params query-params)
           query        (merge base-params limit-params)]
       (log/info :count-ids/query {:query query :params params})
       (let [n (c.xtdb/query-value query params)]
         (log/info :count-ids/finished {:n n})
         (or n 0))))))

(>defn index-ids
  ([]
   [=> (s/coll-of ::m.rates/id)]
   (index-ids {}))
  ([query-params]
   [map? => (s/coll-of ::m.rates/id)]
   (do
     (log/debug :index-ids/starting {})
     (let [{:indexed-access/keys [options]}                 query-params
           {:keys [limit offset] :or {limit 20 offset 0}} options
           base-params                                      (get-index-query query-params)
           limit-params                                     {:limit limit :offset offset}
           query                                            (merge base-params limit-params)
           params                                           (get-index-params query-params)]
       (log/info :index-ids/query {:query query :params params})
       (let [ids (c.xtdb/query-values query params)]
         (log/info :index-ids/finished {:ids ids})
         ids)))))

(>defn index-records
  []
  [=> (s/coll-of ::m.rates/item)]
  (map read-record (index-ids)))

(>defn index-records-by-currency
  [currency-id]
  [:xt/id => ::m.rates/rate-feed]
  (let [db    (c.xtdb/get-db)
        query '{:find  [?date ?rate]
                :in    [[?currency-id]]
                :where [[?rate-id ::m.rates/currency ?currency-id]
                        [?rate-id ::m.rates/rate ?rate]
                        [?rate-id ::m.rates/date ?date]]}]
    (->> (xt/q db query [currency-id])
         (sort-by first)
         (reverse)
         (take record-limit)
         (map (fn [[date rate]] [(.getTime date) rate])))))

(>defn delete-record
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/submit-tx node [[:db/retractEntity id]]))
  nil)
