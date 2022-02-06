(ns dinsro.actions.core-peers
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.core-peers :as m.core-peers]
   [dinsro.queries.core-nodes :as q.core-nodes]
   [dinsro.queries.core-peers :as q.core-peers]
   [lambdaisland.glogc :as log]))

(defn create!
  "Create a new peer connection for this node"
  [props]
  (log/info :peer/creating {:props props}))

(>defn get-peer-info
  "Fetch peer info for node"
  [node]
  [::m.core-nodes/item => any?]
  (let [client (m.core-nodes/get-client node)]
    (c.bitcoin/get-peer-info client)))

(>defn fetch-peers!
  "Fetch and update peers for node"
  [node]
  [::m.core-nodes/item => any?]
  (log/info :peers/fetching {})
  (let [node-id (::m.core-nodes/id node)
        client  (m.core-nodes/get-client node)]
    (doseq [peer (c.bitcoin/get-peer-info client)]
      (let [params (assoc peer ::m.core-peers/node node-id)
            params (m.core-peers/prepare-params params)]
        (q.core-peers/create-record params)))))

(>defn add-peer!
  [node address]
  [::m.core-nodes/item string? => any?]
  (log/info :peer/adding {:node-id (::m.core-nodes/id node) :address address})
  (let [client (m.core-nodes/get-client node)]
    (c.bitcoin/add-node client address)))

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

  node2

  nil)
