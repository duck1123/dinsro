(ns dinsro.actions.core.nodes
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.blocks :as a.c.blocks]
   [dinsro.actions.core.peers :as a.c.peers]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.peers :as q.c.peers]
   [dinsro.queries.core.tx :as q.c.tx]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.rpc.config.BitcoindAuthCredentials
   org.bitcoins.rpc.config.BitcoindAuthCredentials$PasswordBased
   org.bitcoins.rpc.config.BitcoindInstanceRemote
   org.bitcoins.rpc.config.ZmqConfig
   org.bitcoins.rpc.client.v22.BitcoindV22RpcClient
   java.net.URI
   akka.actor.ActorSystem
   scala.Option
   scala.PartialFunction
   scala.Function1
   scala.concurrent.ExecutionContext
   scala.concurrent.Future))

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
  (let [block-response (a.c.blocks/fetch-blocks node)
        peer-response  (a.c.peers/fetch-peers! node)
        updated-node   (q.c.nodes/read-record id)]
    (log/debug
     :node/fetching-finished
     {:block-response     block-response
      :peer-response      peer-response
      :update-node updated-node})
    updated-node))

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

(defn get-remote-uri
  [node]
  (URI.
   (str
    "http://"
    (::m.c.nodes/host node)
    ":"

    "18444"

    #_(::m.c.nodes/port node))))

(defn get-rpc-uri
  [node]
  (URI.
   (str "http://" (::m.c.nodes/host node)
        ":"
        (::m.c.nodes/port node))))

(defn get-zmq-config
  []
  (ZmqConfig/empty))

(defn get-auth-credentials
  [{::m.c.nodes/keys [rpcuser rpcpass]}]
  (BitcoindAuthCredentials$PasswordBased. rpcuser rpcpass))

(defn get-remote-instance
  [node]
  (BitcoindInstanceRemote/apply
   (c.bitcoin-s/regtest-network)
   (get-remote-uri node)
   (get-rpc-uri node)
   (get-auth-credentials node)
   (get-zmq-config)
   (Option/empty)
   (ActorSystem/apply)))

(defn await-future
  [^Future f]
  (.onComplete
   f
   (reify Function1
     (apply [this a]
       (log/info :pf/apply {:a (.get a)})))

   (ExecutionContext/global)))

(comment
  (tap> (q.c.nodes/index-records))

  (def node-alice (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-alice")))
  (def node-bob (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-bob")))
  (def node node-alice)
  (def node-id (::m.c.nodes/id node))
  (def client (m.c.nodes/get-client node))
  (c.bitcoin/get-peer-info client)

  (tap> node)
  node

  (tap> (c.bitcoin/get-blockchain-info client))
  (c.bitcoin/add-node client "bitcoin.bitcoin-bob")

  (generate-to-address! node "bcrt1qyyvtjwguj3z6dlqdd66zs2zqqe6tp4qzy0cp6g")

  (c.bitcoin/get-new-address client)
  (c.bitcoin/create-wallet client "foo")

  (c.bitcoin/get-wallet-info client)

  (fetch-transactions! node)

  (q.c.peers/index-ids)

  (get-auth-credentials node)

  (get-rpc-uri node)
  (get-remote-uri node)

  (c.bitcoin-s/regtest-network)

  (get-remote-instance node)

  (def client-s (BitcoindV22RpcClient/apply (get-remote-instance node)))

  (def pf (.ping client-s))

  (await-future pf)

  (await-future (.getPeerInfo client-s))

  (.value pf)

  (.foreach pf (fn [a] (log/info :ping/response {:a a})))

  (.andThen pf
            (reify PartialFunction
              (apply [this a]
                (log/info :pf/apply {:a a}))))

  (.onComplete
   pf
   (reify Function1
     (apply [this a]
       (log/info :pf/apply {:a a})))

   (ExecutionContext/global))

  (ExecutionContext/global)

  (prn (vec (seq (.getMethods (class pf)))))

  (class pf)

  (.getParameters (first (seq (.getMethods BitcoindInstanceRemote))))

  nil)
