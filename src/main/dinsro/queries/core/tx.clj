(ns dinsro.queries.core.tx
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.blocks :as m.core-blocks]
   [dinsro.model.core.nodes :as m.core-nodes]
   [dinsro.model.core.tx :as m.core-tx]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.core-tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.core-tx/id _]]}]
    (map first (xt/q db query))))

(>defn find-by-node
  [node-id]
  [::m.core-nodes/id => (s/coll-of ::m.core-tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?tx-id]
                :in    [?node-id]
                :where [[?tx-id ::m.core-tx/node ?node-id]]}]
    (map first (xt/q db query node-id))))

(>defn find-by-block
  [block-id]
  [::m.core-blocks/id => (s/coll-of ::m.core-tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?tx-id]
                :in    [?block-id]
                :where [[?tx-id ::m.core-tx/block ?block-id]]}]
    (map first (xt/q db query block-id))))

(>defn fetch-by-txid
  [tx-id]
  [::m.core-tx/tx-id => (? ::m.core-tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [?tx-id]
                :where [[?id ::m.core-tx/tx-id ?tx-id]]}]
    (ffirst (xt/q db query tx-id))))

(>defn read-record
  [id]
  [::m.core-tx/id => (? ::m.core-tx/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.core-tx/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.core-tx/params => ::m.core-tx/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.core-tx/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.core-tx/item)]
  (map read-record (index-ids)))

(>defn update-tx
  [id data]
  [::m.core-tx/id ::m.core-tx/params => ::m.core-tx/id]
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)
    id))

(>defn delete
  [id]
  [::m.core-tx/id => any?]
  (let [node (c.xtdb/main-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(comment
  2
  :the
  (first (index-records))

  (index-ids)
  (map delete (index-ids))

  nil)
