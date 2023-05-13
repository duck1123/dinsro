(ns dinsro.queries.core.peers
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.peers/id)]
  (c.xtdb/query-values '{:find [?e] :where [[?e ::m.c.peers/id _]]}))

(>defn read-record
  [id]
  [::m.c.peers/id => (? ::m.c.peers/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.peers/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.peers/params => ::m.c.peers/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.peers/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn delete!
  [id]
  [::m.c.peers/id => any?]
  (log/debug :delete!/starting {:id id})
  (let [node (c.xtdb/get-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(>defn find-by-core-node
  [node-id]
  [::m.c.nodes/id => (s/coll-of ::m.c.peers/id)]
  (log/debug :find-by-core-node/starting {:node-id node-id})
  (c.xtdb/query-values
   '{:find  [?peer-id]
     :in    [[?node-id]]
     :where [[?peer-id ::m.c.peers/node ?node-id]]}
   [node-id]))

(>defn find-by-node-and-peer-id
  [node-id peer-id]
  [::m.c.peers/node ::m.c.peers/peer-id => (? ::m.c.peers/id)]
  (log/debug :find-by-node-and-peer-id/starting {:node-id node-id :peer-id peer-id})
  (c.xtdb/query-value
   '{:find  [?id]
     :in    [[?node-id ?peer-id]]
     :where [[?id ::m.c.peers/node ?node-id]
             [?id ::m.c.peers/peer-id ?peer-id]]}
   [node-id peer-id]))
