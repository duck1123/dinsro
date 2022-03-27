(ns dinsro.queries.core.tx
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.tx/id _]]}]
    (map first (xt/q db query))))

(>defn find-by-node
  [node-id]
  [::m.c.nodes/id => (s/coll-of ::m.c.tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?tx-id]
                :in    [?node-id]
                :where [[?tx-id ::m.c.tx/node ?node-id]]}]
    (map first (xt/q db query node-id))))

(>defn find-by-block
  [block-id]
  [::m.c.blocks/id => (s/coll-of ::m.c.tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?tx-id]
                :in    [?block-id]
                :where [[?tx-id ::m.c.tx/block ?block-id]]}]
    (map first (xt/q db query block-id))))

(>defn fetch-by-txid
  [tx-id]
  [::m.c.tx/tx-id => (? ::m.c.tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [?tx-id]
                :where [[?id ::m.c.tx/tx-id ?tx-id]]}]
    (ffirst (xt/q db query tx-id))))

(>defn read-record
  [id]
  [::m.c.tx/id => (? ::m.c.tx/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.tx/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.tx/params => ::m.c.tx/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.tx/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.tx/item)]
  (map read-record (index-ids)))

(>defn update-tx
  [id data]
  [::m.c.tx/id ::m.c.tx/params => ::m.c.tx/id]
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)
    id))

(>defn delete
  [id]
  [::m.c.tx/id => any?]
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
