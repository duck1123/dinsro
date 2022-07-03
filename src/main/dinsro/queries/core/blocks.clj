(ns dinsro.queries.core.blocks
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.blocks/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.blocks/id _]]}]
    (map first (xt/q db query))))

(>defn find-by-node
  [node-id]
  [::m.c.nodes/id => (s/coll-of ::m.c.blocks/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?block-id]
                :in    [?node-id]
                :where [[?block-id ::m.c.blocks/node ?node-id]]}]
    (map first (xt/q db query node-id))))

(>defn fetch-by-node-and-height
  [node-id height]
  [::m.c.blocks/node ::m.c.blocks/height => (? ::m.c.blocks/id)]
  (log/finer :fetch-by-node-and-height/starting {:node-id node-id :height height})
  (let [db       (c.xtdb/main-db)
        query    '{:find  [?id]
                   :in    [?node-id ?height]
                   :where [[?id ::m.c.blocks/node ?node-id]
                           [?id ::m.c.blocks/height ?height]]}
        response (ffirst (xt/q db query node-id height))]
    (log/info :fetch-by-node-and-height/finished {:node-id node-id :height height :response response})
    response))

(>defn read-record
  [id]
  [::m.c.blocks/id => (? ::m.c.blocks/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.blocks/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.blocks/params => ::m.c.blocks/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.blocks/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.blocks/item)]
  (map read-record (index-ids)))

(>defn update-block
  [id data]
  [::m.c.blocks/id ::m.c.blocks/params => any?]
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)
    id))

(>defn delete
  [id]
  [::m.c.blocks/id => any?]
  (let [node   (c.xtdb/main-node)
        tx     (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(>defn find-by-tx
  [tx-id]
  [::m.c.tx/id => (? ::m.c.blocks/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?block-id]
                :in    [?tx-id]
                :where [[?tx-id ::m.c.tx/block ?block-id]]}]
    (ffirst (xt/q db query tx-id))))

(comment
  2
  :the
  (first (index-records))

  (map delete (index-ids))

  nil)
