(ns dinsro.actions.core.peers
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.node-base :as a.c.node-base]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.peers :as q.c.peers]
   [lambdaisland.glogc :as log]))

(>defn get-peer-info
  "Fetch peer info for node"
  [node]
  [::m.c.nodes/item => any?]
  (let [client (a.c.node-base/get-client node)]
    (c.bitcoin-s/get-peer-info client)))

(defn update-peer!
  [node-id peer]
  (log/info :update-peer!/starting {:node-id node-id :peer peer})
  (if-let [peer-index (:id peer)]
    (do
      (log/info :update-peer!/got-index {:peer-index peer-index :node-id node-id :peer peer})
      (if-let [existing-peer (q.c.peers/find-by-node-and-peer-id node-id peer-index)]
        (let [peer-id (::m.c.peers/id existing-peer)]
          (log/info :update-peer!/record-exists
                    {:node-id    node-id
                     :peer-index peer-index
                     :peer-id    peer-id})
          peer-id)
        (do
          (log/info :update-peer!/record-not-exists {:node-id node-id :peer-index peer-index})
          (let [params (assoc peer ::m.c.peers/node node-id)
                params (m.c.peers/prepare-params params)]
            (log/info :update-peer!/params-prepared {:params params})
            (q.c.peers/create-record params)))))
    (do
      (log/error :update-peer!/peer-index-missing {:node-id node-id :peer peer})
      (throw (RuntimeException. "Failed to find peer id")))))

(>defn fetch-peers!
  "Fetch and update peers for node"
  [node]
  [::m.c.nodes/item => any?]
  (let [node-id (::m.c.nodes/id node)]
    (log/info :fetch-peers!/starting {:node-id node-id})
    (let [client  (a.c.node-base/get-client node)]
      (doseq [peer (c.bitcoin-s/get-peer-info client)]
        (update-peer! node-id peer)))))

(>defn add-peer!
  [node address]
  [::m.c.nodes/item string? => any?]
  (log/info :add-peer!/starting {:node-id (::m.c.nodes/id node) :address address})
  (let [client   (a.c.node-base/get-client node)
        response (c.bitcoin-s/add-node client address)]
    (log/info :add-peer!/finished {:response response})
    response))

(defn create!
  "Create a new peer connection for this node"
  [{addr    ::m.c.peers/addr
    node-id ::m.c.peers/node}]
  (log/info :create!/starting {:node-id node-id :addr addr})
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
      (log/info :delete!/starting {:node-id node-id :peer-id peer-id})
      (if-let [node (q.c.nodes/read-record node-id)]
        (let [client (a.c.node-base/get-client node)]
          (c.bitcoin-s/disconnect-node client addr)
          (q.c.peers/delete! peer-id))
        (do
          (log/warn :delete!/node-not-found {:peer-id peer-id :node-id :node-id})
          nil)))
    (do
      (log/warn :delete!/peer-not-found {:peer-id peer-id})
      nil)))
