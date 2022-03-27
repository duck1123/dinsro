(ns dinsro.queries.core.blocks
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.nodes :as m.core-nodes]
   [dinsro.model.core.blocks :as m.core-blocks]
   [dinsro.model.core.tx :as m.core-tx]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.core-blocks/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.core-blocks/id _]]}]
    (map first (xt/q db query))))

(>defn find-by-node
  [node-id]
  [::m.core-nodes/id => (s/coll-of ::m.core-blocks/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?block-id]
                :in    [?node-id]
                :where [[?block-id ::m.core-blocks/node ?node-id]]}]
    (map first (xt/q db query node-id))))

(>defn fetch-by-node-and-height
  [node-id height]
  [::m.core-blocks/node ::m.core-blocks/height => (? ::m.core-blocks/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [?node-id ?height]
                :where [[?id ::m.core-blocks/node ?node-id]
                        [?id ::m.core-blocks/height ?height]]}]
    (ffirst (xt/q db query node-id height))))

(>defn read-record
  [id]
  [::m.core-blocks/id => (? ::m.core-blocks/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.core-blocks/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.core-blocks/params => ::m.core-blocks/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.core-blocks/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.core-blocks/item)]
  (map read-record (index-ids)))

(>defn update-block
  [id data]
  [::m.core-blocks/id ::m.core-blocks/params => any?]
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)
    id))

(>defn delete
  [id]
  [::m.core-blocks/id => any?]
  (let [node   (c.xtdb/main-node)
        tx     (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(>defn find-by-tx
  [tx-id]
  [::m.core-tx/id => (? ::m.core-blocks/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?block-id]
                :in    [?tx-id]
                :where [[?tx-id ::m.core-tx/block ?block-id]]}]
    (ffirst (xt/q db query tx-id))))

(comment
  2
  :the
  (first (index-records))

  (map delete (index-ids))

  nil)
