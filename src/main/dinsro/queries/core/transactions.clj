(ns dinsro.queries.core.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.transactions/id)]
  (c.xtdb/query-ids '{:find [?e] :where [[?e ::m.c.transactions/id _]]}))

(>defn find-by-node
  [node-id]
  [::m.c.nodes/id => (s/coll-of ::m.c.transactions/id)]
  (log/info :find-by-node/starting {:node-id node-id})
  (c.xtdb/query-ids
   '{:find  [?tx-id]
     :in    [[?node-id]]
     :where [[?tx-id ::m.c.transactions/node ?node-id]]}
   [node-id]))

(>defn find-by-block
  [block-id]
  [::m.c.blocks/id => (s/coll-of ::m.c.transactions/id)]
  (log/info :find-by-block/starting {:block-id block-id})
  (c.xtdb/query-ids
   '{:find  [?tx-id]
     :in    [[?block-id]]
     :where [[?tx-id ::m.c.transactions/block ?block-id]]}
   [block-id]))

(>defn fetch-by-txid
  [tx-id]
  [::m.c.transactions/tx-id => (? ::m.c.transactions/id)]
  (c.xtdb/query-id
   '{:find  [?id]
     :in    [[?tx-id]]
     :where [[?id ::m.c.transactions/tx-id ?tx-id]]}
   [tx-id]))

(>defn read-record
  [id]
  [::m.c.transactions/id => (? ::m.c.transactions/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.transactions/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.transactions/params => ::m.c.transactions/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.transactions/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.transactions/item)]
  (map read-record (index-ids)))

(>defn update-tx
  [id data]
  [::m.c.transactions/id ::m.c.transactions/params => ::m.c.transactions/id]
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)
    id))

(>defn delete
  [id]
  [::m.c.transactions/id => any?]
  (let [node (c.xtdb/main-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(>defn find-by-ln-node
  [ln-node-id]
  [::m.ln.nodes/id => (? ::m.c.transactions/id)]
  (comment ln-node-id)
  ;;  (c.xtdb/query-id
  ;;   '{:find  [?id]
  ;;     :in    [[?ln-node-id]]
  ;;     :where [[]
  ;;             [?id ::m.c.transactions/tx-id ?tx-id]]}
  ;;   [ln-node-id])
  (throw (ex-info "not implemented" {})))
