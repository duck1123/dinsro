(ns dinsro.queries.rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.streams :as streams]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [manifold.stream :as ms]
   [tick.alpha.api :as t]
   [xtdb.api :as xt]))

;; [../joins/rates.cljc]

(def query-info
  "Query info for Debits"
  {:ident        ::m.rates/id
   :pk           '?rate-id
   :clauses      [[::m.currencies/id   '?currency-id]
                  [::m.rate-sources/id '?rate-source-id]]
   :sort-columns {::m.rates/rate   '?rate-value
                  ::m.rates/source '?source-name
                  ::m.rates/date   '?rate-date}
   :rules
   (fn [[currency-id rate-source-id] rules]
     (->> rules
          (concat-when rate-source-id
            [['?rate-id                 ::m.rates/source          '?rate-source-id]])
          (concat-when currency-id
            [['?rate-id                 ::m.rates/source          '?currency-rate-source-id]
             ['?currency-rate-source-id ::m.rate-sources/currency '?currency-id]])))})

(defn count-ids
  "Count rate records"
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  "Index rate records"
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

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
     :where [[?rate-id        ::m.rates/source          ?rate-source-id]
             [?rate-source-id ::m.rate-sources/currency ?currency-id]]}
   [currency-id]))

(>defn find-top-by-currency
  [currency-id]
  [::m.currencies/id => (? ::m.rates/id)]
  (c.xtdb/query-value
   '{:find     [?rate-id ?date]
     :in       [[?currency-id]]
     :where    [[?rate-id        ::m.rates/source          ?rate-source-id]
                [?rate-id        ::m.rates/date            ?date]
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
                            (update ::m.rates/date t/inst))]
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
          (update ::m.rates/date t/instant)
          (dissoc :xt/id)))))

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
                        [?rate-id ::m.rates/rate     ?rate]
                        [?rate-id ::m.rates/date     ?date]]}]
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

(>defn find-for-debit
  "Find the most recent rate for the currency before the transaction date"
  [debit-id]
  [::m.debits/id => (? ::m.rates/id)]
  (log/trace :find-for-debit/starting {:debit-id debit-id})
  (let [query {:find     ['?rate-id '?rate-date]
               :in       [['?debit-id]]
               :where    [['?debit-id       ::m.debits/account     '?account-id]
                          ['?debit-id       ::m.debits/transaction '?transaction-id]
                          ['?account-id     ::m.accounts/currency  '?currency-id]
                          ['?transaction-id ::m.transactions/date  '?transaction-date]
                          ['?rate-id        ::m.rates/currency     '?currency-id]
                          ['?rate-id        ::m.rates/date         '?rate-date]
                          ['(< ?rate-date ?transaction-date)]]
               :order-by [['?rate-date :desc]]}
        params [debit-id]]
    (log/info :find-for-debit/query {:query query :params params})
    (let [id (c.xtdb/query-value query params)]
      (log/debug :find-for-debit/finished {:id id})
      id)))
