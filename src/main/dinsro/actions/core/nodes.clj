(ns dinsro.actions.core.nodes
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.blocks :as a.c.blocks]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.peers :as q.c.peers]
   [dinsro.queries.core.tx :as q.c.tx]
   [lambdaisland.glogc :as log]))

(def sample-address "bcrt1qyyvtjwguj3z6dlqdd66zs2zqqe6tp4qzy0cp6g")

(defn generate-to-address!
  "Generate regtest blocks paying to address"
  [node address]
  (let [client (m.c.nodes/get-client node)]
    (c.bitcoin/generate-to-address client address)))

(>defn get-blockchain-info
  "Fetch blockchain info for node"
  [node]
  [::m.c.nodes/item => any?]
  (let [client (m.c.nodes/get-client node)
        info   (c.bitcoin/get-blockchain-info client)]
    info))

(>defn update-blockchain-info!
  "Update node's blockchain info"
  [node]
  [::m.c.nodes/item => any?]
  (log/debug :blockchain-info/updating {:node-id (::m.c.nodes/id node)})
  (let [{::m.c.nodes/keys [id]} node
        params                     (get-blockchain-info node)
        params                     (merge node params)
        params                     (m.c.nodes/prepare-params params)
        response                   (q.c.nodes/update-blockchain-info id params)]
    {:status   :ok
     :response response}))

(>defn fetch!
  "Fetch all updates for node"
  [{::m.c.nodes/keys [id] :as node}]
  [::m.c.nodes/item => ::m.c.nodes/item]
  (log/debug :node/fetching {:node-id id})
  (update-blockchain-info! node)
  (a.c.blocks/fetch-blocks node)
  (q.c.nodes/read-record id))

(>defn fetch-transactions!
  "Fetch transactions for a node's wallet"
  [{node-id ::m.c.nodes/id :as node}]
  [::m.c.nodes/item => any?]
  (log/info :transactions/fetching {:node-id node-id})
  (let [client (m.c.nodes/get-client node)]
    (doseq [txes (c.bitcoin/list-transactions client)]
      (log/debug :transactions/processing-fetched {:txes txes})
      (let [params (assoc txes ::m.c.peers/node node-id)
            params (m.c.tx/prepare-params params)]
        (q.c.tx/create-record params)))))

(>defn generate!
  "Generate a block for the node"
  [node-id]
  [::m.c.nodes/id => any?]
  (log/info :generate/started {:node-id node-id})
  (if-let [node (q.c.nodes/read-record node-id)]
    (let [client  (m.c.nodes/get-client node)
          address sample-address]
      (c.bitcoin/generate-to-address client address))
    (do
      (log/error :generate/node-not-found {:node-id node-id})
      nil)))

(comment
  (tap> (q.c.nodes/index-records))

  (def node-alice (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-alice")))
  (def node-bob (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-bob")))
  (def node node-alice)
  (def node-id (::m.c.nodes/id node))
  (def client (m.c.nodes/get-client node))
  (c.bitcoin/get-peer-info client)

  (tap> node)
  (tap> (c.bitcoin/get-blockchain-info client))
  (c.bitcoin/add-node client "bitcoin.bitcoin-bob")

  (generate-to-address! node "bcrt1qyyvtjwguj3z6dlqdd66zs2zqqe6tp4qzy0cp6g")

  (c.bitcoin/get-new-address client)
  (c.bitcoin/create-wallet client "foo")

  (c.bitcoin/get-wallet-info client)

  (fetch-transactions! node)

  (q.c.peers/index-ids)

  (add-tap {:foo "foo"})

  (tap> "foo")

  nil)