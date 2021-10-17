(ns dinsro.queries.ln-peers
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
   [dinsro.model.ln-info :as m.ln-info]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.ln-peers :as m.ln-peers]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as log]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.ln-peers/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?e]
                :where [[?e ::m.ln-peers/id _]]}]
    (map first (crux/q db query))))

(>defn read-record
  [id]
  [::m.ln-peers/id => (? ::m.ln-peers/item)]
  (let [db     (c.crux/main-db)
        record (crux/pull db '[*] id)]
    (when (get record ::m.ln-peers/id)
      (dissoc record :crux.db/id))))

(>defn create-record
  [params]
  [::m.ln-peers/params => ::m.ln-peers/id]
  (let [node (c.crux/main-node)
        id   (utils/uuid)
        peer (-> params
                 (assoc ::m.ln-peers/id id)
                 (assoc :crux.db/id id))
        tx   (crux/submit-tx node [[:crux.tx/put peer]])]
    (crux/await-tx node tx)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.ln-peers/item)]
  (map read-record (index-ids)))

(>defn find-peer
  [node-id pubkey]
  [::m.ln-nodes/id ::m.ln-info/identity-pubkey => (? ::m.ln-peers/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?peer-id]
                :in    [[?node-id ?pubkey]]
                :where [[?peer-id ::m.ln-peers/node ?node-id]
                        [?node-id ::m.ln-nodes/pubkey ?pubkey]]}]
    (ffirst (crux/q db query [node-id pubkey]))))

(>defn add-peer!
  [node-id peer]
  [::m.ln-nodes/id ::m.ln-peers/params => ::m.ln-peers/item]
  (log/debugf "Adding Peer: %s" node-id)
  (let [peer-id (create-record (merge peer {::m.ln-peers/node node-id}))]
    (read-record peer-id)))

(>defn find-ids-by-node
  [node-id]
  [::m.ln-nodes/id => (s/coll-of ::m.ln-peers/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?peer-id]
                :in    [?node-id]
                :where [[?peer-id ::m.ln-peers/node ?node-id]]}]
    (map first (crux/q db query node-id))))
