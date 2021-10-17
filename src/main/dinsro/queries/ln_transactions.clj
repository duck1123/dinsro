(ns dinsro.queries.ln-transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.ln-transactions :as m.ln-tx]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as log]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.ln-tx/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?e]
                :where [[?e ::m.ln-tx/id _]]}]
    (map first (crux/q db query))))

(>defn find-ids-by-node
  [node-id]
  [::m.ln-nodes/id => (s/coll-of ::m.ln-tx/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?tx-id]
                :in    [?node-id]
                :where [[?tx-id ::m.ln-tx/node ?node-id]]}]
    (map first (crux/q db query node-id))))

(>defn find-id-by-node-and-tx-hash
  [node-id tx-hash]
  [::m.ln-nodes/id ::m.ln-tx/tx-hash => (? ::m.ln-tx/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?tx-id]
                :in    [[?node-id ?tx-hash]]
                :where [[?tx-id ::m.ln-tx/node ?node-id]
                        [?tx-is ::m.ln-tx/tx-hash ?tx-hash]]}]
    (ffirst (crux/q db query [node-id tx-hash]))))

(>defn read-record
  [id]
  [::m.ln-tx/id => (? ::m.ln-tx/item)]
  (let [db     (c.crux/main-db)
        record (crux/pull db '[*] id)]
    (when (get record ::m.ln-tx/id)
      (dissoc record :crux.db/id))))

(>defn create-record
  [params]
  [::m.ln-tx/params => ::m.ln-tx/id]
  (let [node            (c.crux/main-node)
        id              (utils/uuid)
        prepared-params (-> params
                            (assoc ::m.ln-tx/id id)
                            (assoc :crux.db/id id))
        resp            (crux/submit-tx node [[:crux.tx/put prepared-params]])]
    (crux/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.ln-tx/item)]
  (map read-record (index-ids)))

(>defn add-tx
  [id tx]
  [::m.ln-nodes/id ::m.ln-tx/params => ::m.ln-tx/item]
  (let [params (merge {::m.ln-tx/id id} tx)
        id     (create-record params)]
    (read-record id)))

(>defn find-tx
  [id _tx]
  [::m.ln-nodes/id ::m.ln-tx/params => ::m.ln-tx/item]
  (read-record id))
