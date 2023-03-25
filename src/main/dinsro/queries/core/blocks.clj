(ns dinsro.queries.core.blocks
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.blocks/id)]
  (log/info :index-ids/starting {})
  (c.xtdb/query-ids '{:find [?block-id] :where [[?block-id ::m.c.blocks/id _]]}))

(>defn fetch-by-node-and-height
  [node-id height]
  [::m.c.nodes/id ::m.c.blocks/height => (? ::m.c.blocks/id)]
  (log/info :fetch-by-node-and-height/starting {:node-id node-id :height height})
  (c.xtdb/query-id
   '{:find  [?block-id]
     :in    [[?node-id ?height]]
     :where [[?node-id ::m.c.nodes/network ?network-id]
             [?block-id ::m.c.blocks/network ?network-id]
             [?block-id ::m.c.blocks/height ?height]]}
   [node-id height]))

(>defn fetch-by-network-and-height
  [network-id height]
  [::m.c.networks/id ::m.c.blocks/height => (? ::m.c.blocks/id)]
  (log/info :fetch-by-network-and-height/starting {:network-id network-id :height height})
  (c.xtdb/query-id
   '{:find  [?id]
     :in    [[?network-id ?height]]
     :where [[?id ::m.c.blocks/network ?network-id]
             [?id ::m.c.blocks/height ?height]]}
   [network-id height]))

(>defn read-record
  [id]
  [::m.c.blocks/id => (? ::m.c.blocks/item)]
  (log/info :read-record/starting {:id id})
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.blocks/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.blocks/params => ::m.c.blocks/id]
  (log/info :create-record/starting {:params params})
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.blocks/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    (log/info :create-record/finished {:id id})
    id))

(>defn update-block
  [id data]
  [::m.c.blocks/id any? => ::m.c.blocks/id]
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
  [::m.c.transactions/id => (? ::m.c.blocks/id)]
  (c.xtdb/query-id
   '{:find  [?block-id]
     :in    [[?tx-id]]
     :where [[?tx-id ::m.c.transactions/block ?block-id]]}
   [tx-id]))

(>defn find-by-node
  "Returns all blocks belonging to the network this node belongs to"
  [node-id]
  [::m.c.nodes/id => (s/coll-of ::m.c.blocks/id)]
  (log/info :find-by-node/starting {:node-id node-id})
  (c.xtdb/query-ids
   '{:find  [?block-id]
     :in    [[?node-id]]
     :where [[?node-id ::m.c.nodes/network ?network-id]
             [?block-id ::m.c.blocks/network ?network-id]]}
   [node-id]))
