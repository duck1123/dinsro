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
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.peers/id _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [::m.c.peers/id => (? ::m.c.peers/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.peers/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.peers/params => ::m.c.peers/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.peers/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.peers/item)]
  (log/info :peers/indexing {})
  (map read-record (index-ids)))

(>defn delete!
  [id]
  [::m.c.peers/id => any?]
  (log/info :peers/deleting {:id id})
  (let [node (c.xtdb/main-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(>defn find-by-core-node
  [node-id]
  [::m.c.nodes/id => (s/coll-of ::m.c.peers/id)]
  (log/info :peers/find-by-node {:node-id node-id})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?peer-id]
                :in    [?node-id]
                :where [[?peer-id ::m.c.peers/node ?node-id]]}
        raw (xt/q db query node-id)]
    (log/info :peers/find-by-node-raw {:raw raw})
    (let [ids (map first raw)]
      (log/info :peers/find-by-node-results {:ids ids})
      ids)))

(>defn find-by-node-and-peer-id
  [node-id peer-id]
  [::m.c.peers/node ::m.c.peers/peer-id => (? ::m.c.peers/id)]
  (log/debug :find-by-node-and-peer-id/starting {:node-id node-id :peer-id peer-id})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [[?node-id ?peer-id]]
                :where [[?id ::m.c.peers/node ?node-id]
                        [?id ::m.c.peers/peer-id ?peer-id]]}
        result (ffirst (xt/q db query [node-id peer-id]))]
    (log/debug :find-by-node-and-peer-id/finished {:result result})
    result))
