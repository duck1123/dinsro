(ns dinsro.actions.rates
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.rates :as q.rates]
   [taoensso.timbre :as log]))

(>defn add-rate
  [source-id rate]
  [::m.rate-sources/id ::m.rates/params => ::m.rates/item]
  (let [params  (assoc rate ::m.rates/source source-id)
        rate-id (q.rates/create-record params)]
    (q.rates/read-record rate-id)))

(comment

  (q.currencies/index-ids)

  (def sats (q.currencies/find-eid-by-code "sats"))
  (def dollars (q.currencies/find-eid-by-code "usd"))

  (q.rates/find-by-currency dollars)
  (q.rates/find-top-by-currency dollars)

  (q.rates/find-by-currency (second (q.currencies/index-ids)))

  nil)
