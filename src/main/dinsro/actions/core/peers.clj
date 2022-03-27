(ns dinsro.actions.core.peers
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.peers :as q.c.peers]
   [lambdaisland.glogc :as log]))

(>defn get-peer-info
  "Fetch peer info for node"
  [node]
  [::m.c.nodes/item => any?]
  (let [client (m.c.nodes/get-client node)]
    (c.bitcoin/get-peer-info client)))

(defn update-peer!
  [node-id peer]
  (let [{peer-index ::m.c.peers/peer-id} peer]
    (if-let [existing-peer (q.c.peers/find-by-node-and-peer-id node-id peer-index)]
      (let [peer-id (::m.c.peers/id existing-peer)]
        (log/info :peer-update/record-exists {:node-id node-id :peer-index peer-index})
        peer-id)
      (do
        (log/info :peer-update/record-exists {:node-id node-id :peer-index peer-index})
        (let [params (assoc peer ::m.c.peers/node node-id)
              params (m.c.peers/prepare-params params)]
          (q.c.peers/create-record params))))))

(>defn fetch-peers!
  "Fetch and update peers for node"
  [node]
  [::m.c.nodes/item => any?]
  (log/info :peers/fetching {})
  (let [node-id (::m.c.nodes/id node)
        client  (m.c.nodes/get-client node)]
    (doseq [peer (c.bitcoin/get-peer-info client)]
      (update-peer! node-id peer))))

(>defn add-peer!
  [node address]
  [::m.c.nodes/item string? => any?]
  (log/info :peer/adding {:node-id (::m.c.nodes/id node) :address address})
  (let [client (m.c.nodes/get-client node)]
    (c.bitcoin/add-node client address)))

(defn create!
  "Create a new peer connection for this node"
  [{addr    ::m.c.peers/addr
    node-id ::m.c.peers/node}]
  (log/info :peer/creating {:node-id node-id :addr addr})
  (if-let [node (q.c.nodes/read-record node-id)]
    (do
      (add-peer! node addr)
      (fetch-peers! node))
    (throw (RuntimeException. (str "Failed to find node: " node-id)))))

(defn delete!
  "Remove node and delete record"
  [{peer-id ::m.c.peers/id}]
  (if-let [peer (q.c.peers/read-record peer-id)]
    (let [{node-id ::m.c.peers/node
           addr    ::m.c.peers/addr} peer]
      (log/info :peer/deleting {:node-id node-id :peer-id peer-id})
      (if-let [node (q.c.nodes/read-record node-id)]
        (let [client (m.c.nodes/get-client node)]
          (c.bitcoin/disconnect-node client addr)
          (q.c.peers/delete! peer-id))
        (do
          (log/warn :peer/delete-node-not-found {:peer-id peer-id :node-id :node-id})
          nil)))
    (do
      (log/warn :peer/delete-not-found {:peer-id peer-id})
      nil)))

(comment

  (def node1 (first (q.c.nodes/index-ids)))
  (def node2 (second (q.c.nodes/index-ids)))
  (map q.c.peers/find-by-core-node (q.c.nodes/index-ids))
  (q.c.nodes/read-record node1)

  (get-peer-info (q.c.nodes/read-record node1))

  (::m.c.nodes/host (q.c.nodes/read-record node2))

  (add-peer!
   (q.c.nodes/read-record node1)
   (::m.c.nodes/host (q.c.nodes/read-record node2)))

  (def peer (first (q.c.peers/index-records)))
  (delete! peer)
  (tap> peer)

  (tap> (q.c.peers/index-records))

  node2

  nil)
