(ns dinsro.actions.core.nodes
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.blocks :as a.c.blocks]
   [dinsro.actions.core.node-base :as a.c.node-base]
   [dinsro.actions.core.peers :as a.c.peers]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.client.scala :as cs]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.transactions :as q.c.transactions]
   [lambdaisland.glogc :as log]))

(def sample-address "bcrt1qyyvtjwguj3z6dlqdd66zs2zqqe6tp4qzy0cp6g")

(>defn generate-to-address!
  "Generate regtest blocks paying to address"
  [node address]
  [::m.c.nodes/item string? => (s/coll-of string?)]
  (log/info :generate-to-address!/starting {:node node :address address})
  (let [client (a.c.node-base/get-client node)
        result (c.bitcoin-s/generate-to-address! client address)]
    (log/info :generate-to-address!/finished {:result result})
    result))

(>defn get-blockchain-info
  "Fetch blockchain info for node"
  [node]
  [::m.c.nodes/item => any?]
  (log/info :get-blockchain-info/starting {:node node})
  (let [client (a.c.node-base/get-client node)
        info   (c.bitcoin-s/get-blockchain-info client)]
    info))

(>defn update-blockchain-info!
  "Update node's blockchain info"
  [node]
  [::m.c.nodes/item => any?]
  (let [{::m.c.nodes/keys [id]} node]
    (log/debug :update-blockchain-info!/starting {:id id})
    (let [params   (get-blockchain-info node)
          params   (merge node params)
          params   (m.c.nodes/prepare-params params)
          response (q.c.nodes/update-blockchain-info id params)]
      {:status   :ok
       :response response})))

(>defn fetch!
  "Fetch all updates for node"
  [{::m.c.nodes/keys [id] :as node}]
  [::m.c.nodes/item => ::m.c.nodes/item]
  (log/debug :fetch!/starting {:id id})
  (update-blockchain-info! node)
  (let [block-response (a.c.blocks/fetch-blocks node)
        peer-response  (a.c.peers/fetch-peers! node)
        updated-node   (q.c.nodes/read-record id)]
    (log/trace :fetch!/finished
      {:block-response block-response
       :peer-response  peer-response
       :updated-node   updated-node})
    updated-node))

(>defn list-transactions
  [client]
  [::a.c.node-base/client => any?]
  (cs/->record (c.bitcoin-s/list-transactions client)))

(>defn fetch-transactions!
  "Fetch transactions for a node's wallet"
  [{node-id ::m.c.nodes/id :as node}]
  [::m.c.nodes/item => any?]
  (log/info :fetch-transactions!/starting {:node-id node-id})
  (let [client (a.c.node-base/get-client node)]
    (doseq [txes (list-transactions client)]
      (log/debug :fetch-transactions!/processing {:txes txes})
      (let [params (assoc txes ::m.c.peers/node node-id)
            params (m.c.transactions/prepare-params params)]
        (q.c.transactions/create-record params)))))

(>defn generate!
  "Generate a block for the node."
  [node-id]
  [::m.c.nodes/id => any?]
  (log/info :generate!/starting {:node-id node-id})
  (if-let [node (q.c.nodes/read-record node-id)]
    (let [address sample-address
          response (generate-to-address! node address)]
      (log/info :generate!/finished {:response response})
      response)
    (do
      (log/error :generate!/node-not-found {:node-id node-id})
      nil)))

(defn do-delete!
  [props]
  (log/info :do-delete!/starting {:props props})
  (throw (ex-info "no implemented" {})))
