(ns dinsro.actions.rates
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.rates :as q.rates]))

(>defn add-rate
  [source-id rate]
  [::m.rate-sources/id ::m.rates/params => ::m.rates/item]
  (let [params  (assoc rate ::m.rates/source source-id)
        rate-id (q.rates/create-record params)]
    (q.rates/read-record rate-id)))

(comment

  (q.currencies/index-ids)

  (def sats (q.currencies/find-by-code "sats"))
  sats
  (def dollars (q.currencies/find-by-code "usd"))

  (q.rates/find-by-currency dollars)
  (q.rates/find-top-by-currency dollars)

  (def source-id (first (q.rate-sources/index-ids)))

  (add-rate source-id 1000)

  (q.rates/find-by-currency (second (q.currencies/index-ids)))

  nil)
