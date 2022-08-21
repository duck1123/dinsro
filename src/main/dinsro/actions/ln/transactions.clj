(ns dinsro.actions.ln.transactions
  (:refer-clojure :exclude [next])
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.transactions :as m.ln.tx]
   [dinsro.queries.ln.transactions :as q.ln.tx]))

(>defn handle-get-transactions-response
  [node-id transaction]
  [::m.ln.nodes/id any? => ::m.ln.tx/id]
  (let [params (m.ln.tx/prepare-params transaction)
        params (assoc params ::m.ln.tx/node node-id)]
    (q.ln.tx/create-record params)))
