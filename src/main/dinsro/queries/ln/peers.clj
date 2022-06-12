(ns dinsro.queries.ln.peers
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.ln.peers/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.ln.peers/id _]]}]
    (map first  (xt/q db query))))

(>defn read-record
  [id]
  [::m.ln.peers/id => (? ::m.ln.peers/item)]
  (let [db     (c.xtdb/main-db)
        record  (xt/pull db '[*] id)]
    (when (get record ::m.ln.peers/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.ln.peers/params => ::m.ln.peers/id]
  (let [node (c.xtdb/main-node)
        id   (new-uuid)
        peer (-> params
                 (assoc ::m.ln.peers/id id)
                 (assoc :xt/id id))
        tx   (xt/submit-tx node [[::xt/put peer]])]
    (xt/await-tx node tx)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.ln.peers/item)]
  (map read-record (index-ids)))

(>defn find-peer
  [node-id pubkey]
  [::m.ln.nodes/id ::m.ln.info/identity-pubkey => (? ::m.ln.peers/id)]
  (log/info :find-peer/starting {:node-id node-id :pubkey pubkey})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?peer-id]
                :in    [[?node-id ?pubkey]]
                :where [[?peer-id ::m.ln.peers/node ?node-id]
                        [?peer-id ::m.ln.peers/pubkey ?pubkey]]}]
    (ffirst (xt/q db query [node-id pubkey]))))

(>defn add-peer!
  [node-id peer]
  [::m.ln.nodes/id ::m.ln.peers/params => ::m.ln.peers/item]
  (log/debug :add-peer!/starting {:node-id node-id})
  (let [peer-id (create-record (merge peer {::m.ln.peers/node node-id}))]
    (read-record peer-id)))

(>defn find-ids-by-node
  [node-id]
  [::m.ln.nodes/id => (s/coll-of ::m.ln.peers/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?peer-id]
                :in    [?node-id]
                :where [[?peer-id ::m.ln.peers/node ?node-id]]}]
    (map first (xt/q db query node-id))))

(>defn find-ids-by-remote-node
  [remote-node-id]
  [::m.ln.peers/remote-node => (s/coll-of ::m.ln.peers/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?peer-id]
                :in    [?remote-node-id]
                :where [[?peer-id ::m.ln.peers/remote-node ?remote-node-id]]}]
    (map first (xt/q db query remote-node-id))))

(>defn delete
  [id]
  [::m.ln.peers/id => any?]
  (log/info :delete/starting {:id id})
  (let [node (c.xtdb/main-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(>defn update!
  [params]
  [::m.ln.peers/item => ::m.ln.peers/id]
  (if-let [id (::m.ln.peers/id params)]
    (let [node   (c.xtdb/main-node)
          params (assoc params :xt/id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)
    (throw (RuntimeException. "Failed to find id"))))
