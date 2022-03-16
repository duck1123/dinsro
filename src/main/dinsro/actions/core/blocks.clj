(ns dinsro.actions.core.blocks
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.tx :as q.c.tx]
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(>defn register-block
  "Create a block reference"
  [node-id hash height]
  [::m.c.blocks/node ::m.c.blocks/hash ::m.c.blocks/height => ::m.c.blocks/id]
  (if-let [block-id (q.c.blocks/fetch-by-node-and-height node-id height)]
    (do
      (log/info :block/found {:node-id node-id :hash hash :height height})
      block-id)
    (do
      (log/info :block/not-found {:node-id node-id :hash hash :height height})
      (let [params {::m.c.blocks/hash     hash
                    ::m.c.blocks/height   height
                    ::m.c.blocks/node     node-id
                    ::m.c.blocks/fetched? false}]
        (q.c.blocks/create-record params)))))

(>defn fetch-block-by-height
  "Fetch a block from the node"
  [node height]
  [::m.c.nodes/item number? => any?]
  (let [client (m.c.nodes/get-client node)]
    (c.bitcoin/fetch-block-by-height client height)))

(>defn update-block!
  "Fetch and update a block from the node"
  [core-node-id height params]
  [::m.c.nodes/id ::m.c.blocks/height any? => (s/tuple ::m.c.blocks/id ::m.c.blocks/params)]
  (if-let [existing-block-id (q.c.blocks/fetch-by-node-and-height core-node-id height)]
    (if (q.c.blocks/read-record existing-block-id)
      (let [params (m.c.blocks/prepare-params params)
            params (assoc params ::m.c.blocks/node core-node-id)
            id     (q.c.blocks/update-block existing-block-id params)]
        [id params])
      (throw (RuntimeException. "cannot find existing block")))
    (let [params (m.c.blocks/prepare-params params)
          params (assoc params ::m.c.blocks/fetched? true)
          params (assoc params ::m.c.blocks/node core-node-id)
          id     (q.c.blocks/create-record params)]
      [id params])))

(>defn update-block-by-height
  "Fetch and update the block by height"
  [node height]
  [::m.c.nodes/item ::m.c.blocks/height => ::m.c.blocks/id]
  (log/info :block/updating {:height height})
  (if-let [core-node-id (::m.c.nodes/id node)]
    (let [client (m.c.nodes/get-client node)]
      (if-let [block (c.bitcoin/fetch-block-by-height client height)]
        (let [previous-hash (:previousblockhash block)
              next-hash     (:nextblockhash block)
              prev-id       (when previous-hash (register-block core-node-id previous-hash (dec height)))
              next-id       (when next-hash (register-block core-node-id next-hash (inc height)))]
          (log/info :block/parsing-neighbors {:previous previous-hash :next next-hash})
          (let [params            block
                params            (assoc params ::m.c.blocks/fetched? true)
                params            (assoc params ::m.c.blocks/hash (:hash block))
                params            (assoc params ::m.c.blocks/height height)
                params            (assoc params ::m.c.blocks/node core-node-id)
                params            (assoc params ::m.c.blocks/next-block next-id)
                params            (assoc params ::m.c.blocks/previous-block prev-id)
                [block-id params] (update-block! core-node-id height params)]
            (doseq [tx-id (::m.c.blocks/tx params)]
              (if-let [existing-id (q.c.tx/fetch-by-txid tx-id)]
                (log/info :tx/exists {:tx-id existing-id})
                (let [params {::m.c.tx/block    block-id
                              ::m.c.tx/tx-id    tx-id
                              ::m.c.tx/fetched? false}]
                  (log/debug :tx/creating {:params params})
                  (q.c.tx/create-record params))))
            block-id))
        (throw (RuntimeException. "no block"))))
    (throw (RuntimeException. "no node id"))))

(>defn fetch-blocks
  "Fetch the latest block for a node"
  [node]
  [::m.c.nodes/item => any?]
  (log/debug :blocks/fetching {:node node})
  (let [client (m.c.nodes/get-client node)
        info   (c.bitcoin/get-blockchain-info client)
        tip    (:blocks info)]
    (log/info :blocks/fetched {:info info})
    (update-block-by-height node tip)
    tip))

(>defn fetch-transactions!
  "Fetch all transactions for a block"
  [block]
  [::m.c.blocks/item => any?]
  (log/info :tx/fetching {:block block})
  (let [block-id (::m.c.blocks/id block)
        node-id  (q.c.nodes/find-by-block block-id)]
    (if-let [node (q.c.nodes/read-record node-id)]
      (let [client (m.c.nodes/get-client node)]
        (doseq [tx (c.bitcoin/list-transactions client)]
          (let [params (m.c.tx/prepare-params tx)]
            (q.c.tx/create-record params))))
      (throw (RuntimeException. "Failed to find node")))))

(>defn search!
  "Find a block. (not implemented)"
  [props]
  [any? => any?]
  (log/info :block/searching {:props props})
  nil)

(comment
  (def node-alice (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-alice")))
  (def node-bob (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-bob")))
  (def node node-alice)

  (tap> (q.c.blocks/index-records))

  (q.c.blocks/index-records)
  (map
   q.c.blocks/read-record
   (q.c.blocks/find-by-node (::m.c.nodes/id node-alice)))
  (q.c.blocks/find-by-node (::m.c.nodes/id node-bob))

  (q.c.blocks/fetch-by-node-and-height (::m.c.nodes/id node-alice) 97)

  nil)
