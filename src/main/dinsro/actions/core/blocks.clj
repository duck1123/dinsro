(ns dinsro.actions.core.blocks
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.model.core.blocks :as m.core-blocks]
   [dinsro.model.core.nodes :as m.core-nodes]
   [dinsro.model.core.tx :as m.core-tx]
   [dinsro.queries.core.blocks :as q.core-blocks]
   [dinsro.queries.core.nodes :as q.core-nodes]
   [dinsro.queries.core.tx :as q.core-tx]
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(>defn register-block
  [node-id hash height]
  [::m.core-blocks/node ::m.core-blocks/hash ::m.core-blocks/height => ::m.core-blocks/id]
  (if-let [block-id (q.core-blocks/fetch-by-node-and-height node-id height)]
    (do
      (log/info :block/found {:node-id node-id :hash hash :height height})
      block-id)
    (do
      (log/info :block/not-found {:node-id node-id :hash hash :height height})
      (let [params {::m.core-blocks/hash     hash
                    ::m.core-blocks/height   height
                    ::m.core-blocks/node     node-id
                    ::m.core-blocks/fetched? false}]
        (q.core-blocks/create-record params)))))

(>defn fetch-block-by-height
  [node height]
  [::m.core-nodes/item number? => any?]
  (let [client (m.core-nodes/get-client node)]
    (c.bitcoin/fetch-block-by-height client height)))

(defn update-block!
  [core-node-id height block]
  (if-let [existing-block-id (q.core-blocks/fetch-by-node-and-height core-node-id height)]
    (if (q.core-blocks/read-record existing-block-id)
      (let [params (m.core-blocks/prepare-params block)
            params (assoc params ::m.core-blocks/node core-node-id)
            id     (q.core-blocks/update-block existing-block-id params)]
        [id params])
      (throw (RuntimeException. "cannot find existing block")))
    (let [params (m.core-blocks/prepare-params block)
          params (assoc params ::m.core-blocks/fetched? true)
          params (assoc params ::m.core-blocks/node core-node-id)
          id     (q.core-blocks/create-record params)]
      [id params])))

(>defn update-block-by-height
  [node height]
  [::m.core-nodes/item ::m.core-blocks/height => ::m.core-blocks/id]
  (log/info :block/updating {:height height})
  (if-let [core-node-id (::m.core-nodes/id node)]
    (let [client (m.core-nodes/get-client node)]
      (if-let [block (c.bitcoin/fetch-block-by-height client height)]
        (let [previous-hash (:previousblockhash block)
              next-hash     (:nextblockhash block)
              prev-id       (when previous-hash (register-block core-node-id previous-hash (dec height)))
              next-id       (when next-hash (register-block core-node-id next-hash (inc height)))]
          (log/info :block/parsing-neighbors {:previous previous-hash :next next-hash})
          (let [block             (assoc block ::m.core-blocks/next-block next-id)
                block             (assoc block ::m.core-blocks/previous-block prev-id)
                [block-id params] (update-block! core-node-id height block)]
            (doseq [tx-id (::m.core-blocks/tx params)]
              (if-let [existing-id (q.core-tx/fetch-by-txid tx-id)]
                (log/info :tx/exists {:tx-id existing-id})
                (let [params {::m.core-tx/block    block-id
                              ::m.core-tx/tx-id    tx-id
                              ::m.core-tx/fetched? false}]
                  (log/debug :tx/creating {:params params})
                  (q.core-tx/create-record params))))
            block-id))
        (throw (RuntimeException. "no block"))))
    (throw (RuntimeException. "no node id"))))

(>defn fetch-blocks
  [node]
  [::m.core-nodes/item => any?]
  (log/debug :blocks/fetching {})
  (let [client (m.core-nodes/get-client node)
        info   (c.bitcoin/get-blockchain-info client)
        tip    (:blocks info)]
    (update-block-by-height node tip)
    tip))

(defn fetch-transactions!
  [block]
  (log/info :tx/fetching {:block block})
  (let [block-id (::m.core-blocks/id block)
        node-id  (q.core-nodes/find-by-block block-id)]
    (if-let [node (q.core-nodes/read-record node-id)]
      (let [client (m.core-nodes/get-client node)]
        (doseq [tx (c.bitcoin/list-transactions client)]
          (let [params (m.core-tx/prepare-params tx)]
            (q.core-tx/create-record params))))
      (throw (RuntimeException. "Failed to find node")))))

(defn search!
  [_props]
  nil)

(comment
  (def node-alice (q.core-nodes/read-record (q.core-nodes/find-id-by-name "bitcoin-alice")))
  (def node-bob (q.core-nodes/read-record (q.core-nodes/find-id-by-name "bitcoin-bob")))
  (def node node-alice)

  (tap> (q.core-blocks/index-records))

  (q.core-blocks/index-records)
  (map
   q.core-blocks/read-record
   (q.core-blocks/find-by-node (::m.core-nodes/id node-alice)))
  (q.core-blocks/find-by-node (::m.core-nodes/id node-bob))

  (q.core-blocks/fetch-by-node-and-height (::m.core-nodes/id node-alice) 97)

  nil)
