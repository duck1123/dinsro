(ns dinsro.actions.core-nodes
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core-block :as a.core-block]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.core-peers :as m.core-peers]
   [dinsro.model.core-tx :as m.core-tx]
   [dinsro.queries.core-nodes :as q.core-nodes]
   [dinsro.queries.core-peers :as q.core-peers]
   [dinsro.queries.core-tx :as q.core-tx]
   [taoensso.timbre :as log]))

(defn generate-to-address!
  [node address]
  (let [client (m.core-nodes/get-client node)]
    (c.bitcoin/generate-to-address client address)))

(>defn get-blockchain-info
  [node]
  [::m.core-nodes/item => any?]
  (let [client (m.core-nodes/get-client node)
        info   (c.bitcoin/get-blockchain-info client)]
    info))

(>defn update-blockchain-info!
  [node]
  [::m.core-nodes/item => any?]
  (log/debug "Update blockchain info")
  (let [{::m.core-nodes/keys [id]} node
        params                     (get-blockchain-info node)
        params                     (merge node params)
        params                     (m.core-nodes/prepare-params params)
        response                   (q.core-nodes/update-blockchain-info id params)]
    {:status   :ok
     :response response}))

(>defn fetch!
  [{::m.core-nodes/keys [id] :as node}]
  [::m.core-nodes/item => ::m.core-nodes/item]
  (log/debug "Fetching from core node")
  (update-blockchain-info! node)
  (a.core-block/fetch-blocks node)
  (q.core-nodes/read-record id))

(defn fetch-peers!
  [node]
  (log/info "fetching peers")
  (let [node-id (::m.core-nodes/id node)
        client  (m.core-nodes/get-client node)]
    (doseq [peer (c.bitcoin/get-peer-info client)]
      (let [params (assoc peer ::m.core-peers/node node-id)
            params (m.core-peers/prepare-params params)]
        (q.core-peers/create-record params)))))

(>defn fetch-transactions!
  [node]
  [::m.core-nodes/item => any?]
  (log/info "fetching transactions")
  (let [node-id (::m.core-nodes/id node)
        client  (m.core-nodes/get-client node)]
    (doseq [txes (log/spy :info (c.bitcoin/list-transactions client))]
      (let [params (assoc txes ::m.core-peers/node node-id)
            params (m.core-tx/prepare-params params)]
        (q.core-tx/create-record params)))))

(comment
  (tap> (q.core-nodes/index-records))

  (def node-alice (q.core-nodes/read-record (q.core-nodes/find-id-by-name "bitcoin-alice")))
  (def node-bob (q.core-nodes/read-record (q.core-nodes/find-id-by-name "bitcoin-bob")))
  (def node node-alice)
  (def node-id (::m.core-nodes/id node))
  (def client (m.core-nodes/get-client node))
  (c.bitcoin/get-peer-info client)

  (tap> node)
  (tap> (c.bitcoin/get-blockchain-info client))
  (c.bitcoin/add-node client "bitcoin.bitcoin-bob")

  (generate-to-address! node "bcrt1qyyvtjwguj3z6dlqdd66zs2zqqe6tp4qzy0cp6g")

  (c.bitcoin/get-new-address client)
  (c.bitcoin/create-wallet client "foo")

  (c.bitcoin/get-wallet-info client)

  (fetch-transactions! node)

  (fetch-peers! node)
  (q.core-peers/index-ids)

  (add-tap {:foo "foo"})

  (tap> "foo")

  nil)
