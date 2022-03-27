(ns dinsro.actions.core.peers
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.model.core.nodes :as m.core-nodes]
   [dinsro.model.core.peers :as m.core-peers]
   [dinsro.queries.core.nodes :as q.core-nodes]
   [dinsro.queries.core.peers :as q.core-peers]
   [lambdaisland.glogc :as log]))

(>defn get-peer-info
  "Fetch peer info for node"
  [node]
  [::m.core-nodes/item => any?]
  (let [client (m.core-nodes/get-client node)]
    (c.bitcoin/get-peer-info client)))

(defn update-peer!
  [node-id peer]
  (let [{peer-index ::m.core-peers/peer-id} peer]
    (if-let [existing-peer (q.core-peers/find-by-node-and-peer-id node-id peer-index)]
      (let [peer-id (::m.core-peers/id existing-peer)]
        (log/info :peer-update/record-exists {:node-id node-id :peer-index peer-index})
        peer-id)
      (do
        (log/info :peer-update/record-exists {:node-id node-id :peer-index peer-index})
        (let [params (assoc peer ::m.core-peers/node node-id)
              params (m.core-peers/prepare-params params)]
          (q.core-peers/create-record params))))))

(>defn fetch-peers!
  "Fetch and update peers for node"
  [node]
  [::m.core-nodes/item => any?]
  (log/info :peers/fetching {})
  (let [node-id (::m.core-nodes/id node)
        client  (m.core-nodes/get-client node)]
    (doseq [peer (c.bitcoin/get-peer-info client)]
      (update-peer! node-id peer))))

(>defn add-peer!
  [node address]
  [::m.core-nodes/item string? => any?]
  (log/info :peer/adding {:node-id (::m.core-nodes/id node) :address address})
  (let [client (m.core-nodes/get-client node)]
    (c.bitcoin/add-node client address)))

(defn create!
  "Create a new peer connection for this node"
  [{addr    ::m.core-peers/addr
    node-id ::m.core-peers/node}]
  (log/info :peer/creating {:node-id node-id :addr addr})
  (if-let [node (q.core-nodes/read-record node-id)]
    (do
      (add-peer! node addr)
      (fetch-peers! node))
    (throw (RuntimeException. (str "Failed to find node: " node-id)))))

(defn delete!
  "Remove node and delete record"
  [{peer-id ::m.core-peers/id}]
  (if-let [peer (q.core-peers/read-record peer-id)]
    (let [{node-id ::m.core-peers/node
           addr    ::m.core-peers/addr} peer]
      (log/info :peer/deleting {:node-id node-id :peer-id peer-id})
      (if-let [node (q.core-nodes/read-record node-id)]
        (let [client (m.core-nodes/get-client node)]
          (c.bitcoin/disconnect-node client addr)
          (q.core-peers/delete! peer-id))
        (do
          (log/warn :peer/delete-node-not-found {:peer-id peer-id :node-id :node-id})
          nil)))
    (do
      (log/warn :peer/delete-not-found {:peer-id peer-id})
      nil)))

(comment

  (def node1 (first (q.core-nodes/index-ids)))
  (def node2 (second (q.core-nodes/index-ids)))
  (map q.core-peers/find-by-core-node (q.core-nodes/index-ids))
  (q.core-nodes/read-record node1)

  (get-peer-info (q.core-nodes/read-record node1))

  (::m.core-nodes/host (q.core-nodes/read-record node2))

  (add-peer!
   (q.core-nodes/read-record node1)
   (::m.core-nodes/host (q.core-nodes/read-record node2)))

  (def peer (first (q.core-peers/index-records)))
  (delete! peer)
  (tap> peer)

  (tap> (q.core-peers/index-records))

  node2

  nil)
