(ns dinsro.actions.core.blocks
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.node-base :as a.c.node-base]
   [dinsro.actions.core.transactions :as a.c.tx]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.client.converters.get-block-result :as c.c.get-block-result]
   [dinsro.client.converters.get-blockchain-info-result :as c.c.get-blockchain-info-result]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.transactions :as q.c.transactions]
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(>defn register-block
  "Create a block reference"
  [node-id hash height]
  [::m.c.nodes/id ::m.c.blocks/hash ::m.c.blocks/height => ::m.c.blocks/id]
  (log/info :register-block/starting {:node-id node-id :hash hash :height height})
  (if-let [network-id (q.c.networks/find-by-node-id node-id)]
    (if-let [block-id (q.c.blocks/fetch-by-network-and-height network-id height)]
      (do
        (log/info :register-block/found {:node-id node-id :hash hash :height height})
        block-id)
      (do
        (log/info :register-block/not-found {:node-id node-id :hash hash :height height})
        (let [params {::m.c.blocks/hash     hash
                      ::m.c.blocks/height   height
                      ::m.c.blocks/network  network-id
                      ::m.c.blocks/fetched? false}]
          (q.c.blocks/create-record params))))
    (throw (ex-info "Failed to find network id" {}))))

(>defn fetch-block-by-height
  "Fetch a block from the node"
  [node height]
  [::m.c.nodes/item number? => any?]
  (let [client (a.c.node-base/get-client node)]
    (c.bitcoin-s/fetch-block-by-height client height)))

(>defn update-block!
  "Fetch and update a block from the node"
  [core-node-id height params]
  [::m.c.nodes/id ::m.c.blocks/height any? => (s/tuple ::m.c.blocks/id ::m.c.blocks/params)]
  (log/info :update-block!/starting {:core-node-id core-node-id :height height :params params})
  (if-let [network-id (q.c.networks/find-by-node-id core-node-id)]
    (if-let [existing-block-id (q.c.blocks/fetch-by-network-and-height network-id height)]
      (if (q.c.blocks/read-record existing-block-id)
        (let [params (assoc params ::m.c.blocks/network network-id)
              params (m.c.blocks/prepare-params params)
              id     (q.c.blocks/update-block existing-block-id params)]
          [id params])
        (do
          (log/error :update-block!/block-not-found {})
          (throw (ex-info "cannot find existing block" {}))))
      (if-let [network-id (q.c.networks/find-by-node-id core-node-id)]
        (let [params (assoc params ::m.c.blocks/network network-id)
              params (assoc params ::m.c.blocks/fetched? true)
              params (m.c.blocks/prepare-params params)
              id     (q.c.blocks/create-record params)]
          [id params])
        (do
          (log/error :update-block!/network-not-found {})
          (throw (ex-info "Failed to find network" {})))))
    (do
      (log/error :update-block!/network-not-found2 {})
      (throw (ex-info "Failed to find network" {})))))

(>defn update-neighbors
  [core-node-id block-id block height]
  [::m.c.nodes/id ::m.c.blocks/id  any? ::m.c.blocks/height => any?]
  (log/finer :update-neighbors/statring {:block-id block-id :block block :height height})
  (let [previous-hash (:dinsro.client.converters.get-block-result/previous-block-hash block)
        next-hash     (:dinsro.client.converters.get-block-result/next-block-hash block)]
    (log/info :update-neighbors/parsing-neighbors {:previous previous-hash :next next-hash})
    (let [prev-id (when previous-hash (register-block core-node-id previous-hash (dec height)))
          next-id (when next-hash (register-block core-node-id next-hash (inc height)))]
      (log/fine :update-neighbors/registered {:prev-id prev-id :next-id next-id})
      (q.c.blocks/update-block block-id {::m.c.blocks/next-block     next-id
                                         ::m.c.blocks/previous-block prev-id}))))

(>defn update-block-by-height
  "Fetch and update the block by height"
  [node height]
  [::m.c.nodes/item ::m.c.blocks/height => ::m.c.blocks/id]
  (log/info :update-block-by-height/updating {:height height})
  (if-let [core-node-id (::m.c.nodes/id node)]
    (if-let [core-node (q.c.nodes/read-record core-node-id)]
      (let [network-id (::m.c.nodes/network core-node)
            client     (a.c.node-base/get-client node)]
        (if-let [block (c.bitcoin-s/fetch-block-by-height client height)]
          (do
            (log/info :update-block-by-height/found-block
                      {:block block :core-node-id core-node-id})
            (if-let [block-record-id (q.c.blocks/fetch-by-network-and-height network-id height)]
              (if-let [block-record (q.c.blocks/read-record block-record-id)]
                (let [updated-id (::m.c.blocks/id block-record)]
                  (log/info :update-block-by-height/found-block-record
                            {:block        block
                             :block-record block-record
                             :core-node-id core-node-id
                             :updated-id   updated-id})
                  (let [params block
                        params (assoc params ::m.c.blocks/fetched? true)
                        params (assoc params ::m.c.blocks/network network-id)
                        params (m.c.blocks/prepare-params params)]
                    (log/info :update-block-by-height/found-params {:params params})
                    (q.c.blocks/update-block updated-id params))
                  (update-neighbors core-node-id block-record-id block height)
                  (doseq [tx-id (::c.c.get-block-result/tx block)]
                    (a.c.tx/register-tx core-node-id updated-id tx-id))
                  updated-id)
                (throw (ex-info "Failed to read record" {})))
              (if-let [update-response (update-block! core-node-id height block)]
                (let [[updated-id] update-response]
                  (log/info :update-block-by-height/updated-block-record
                            {:block        block
                             :core-node-id core-node-id
                             :updated-id   updated-id})
                  (doseq [tx-id (::c.c.get-block-result/tx block)]
                    (a.c.tx/register-tx core-node-id updated-id tx-id))
                  updated-id)
                (throw (ex-info "Updated record returned nil" {})))))
          (throw (ex-info "Failed to fetch block from node" {}))))
      (throw (ex-info "Failed to find node" {})))
    (throw (ex-info "Node does not contain an id." {}))))

(>defn fetch-blocks
  "Fetch the latest block for a node"
  [node]
  [::m.c.nodes/item => any?]
  (log/debug :fetch-blocks/fetching {:node node})
  (let [client (a.c.node-base/get-client node)
        info   (c.bitcoin-s/get-blockchain-info client)]
    (log/info :fetch-blocks/fetched {:info info})
    (if-let [tip (::c.c.get-blockchain-info-result/blocks info)]
      (do
        (log/info :fetch-blocks/fetched {:info info})
        (update-block-by-height node tip)
        tip)
      (throw (ex-info "Failed to determine tip" {})))))

(>defn fetch-transactions!
  "Fetch all transactions for a block"
  [block]
  [::m.c.blocks/item => any?]
  (log/info :fetch-transactions!/fetching {:block block})
  (let [network-id (::m.c.blocks/network block)
        _block-id   (::m.c.blocks/id block)
        node-id    (first (q.c.nodes/find-by-network network-id))]
    (if-let [node (q.c.nodes/read-record node-id)]
      (let [client (a.c.node-base/get-client node)]
             ;; FIXME: Does not fetch transactions for block
        (doseq [tx (c.bitcoin-s/list-transactions client)]
          (let [params (m.c.transactions/prepare-params tx)]
            (q.c.transactions/create-record params))))
      (throw (ex-info "Failed to find node" {})))))

(>defn search!
  "Find a block. (not implemented)"
  [props]
  [any? => any?]
  (log/info :search!/searching {:props props})
  nil)

(defn do-fetch!
  [node-id block-id]
  (log/info :do-fetch!/starting {:block-id block-id :node-id node-id})
  (if-let [block (q.c.blocks/read-record block-id)]
    (let [{height  ::m.c.blocks/height} block]
      (if-let [node (q.c.nodes/read-record node-id)]
        (let [block-id     (update-block-by-height node height)
              updated-item (q.c.blocks/read-record block-id)
              next-id      (::m.c.blocks/next-block updated-item)
              updated-item (if next-id
                             (let [block                             (q.c.blocks/read-record next-id)
                                   {::m.c.blocks/keys [hash height]} block]
                               (assoc updated-item ::m.c.blocks/next-block {::m.c.blocks/id     next-id
                                                                            ::m.c.blocks/hash   hash
                                                                            ::m.c.blocks/height height}))
                             (dissoc updated-item ::m.c.blocks/next-block))
              previous-id  (::m.c.blocks/previous-block updated-item)
              updated-item (if previous-id
                             (let [block                             (q.c.blocks/read-record previous-id)
                                   {::m.c.blocks/keys [hash height]} block]
                               (assoc updated-item ::m.c.blocks/previous-block {::m.c.blocks/id     next-id
                                                                                ::m.c.blocks/hash   hash
                                                                                ::m.c.blocks/height height}))
                             (dissoc updated-item ::m.c.blocks/previous-block))]

          {:status :passed
           :item   updated-item})
        (do
          (log/error :do-fetch!/node-not-found {})
          {:status  :failed
           :message "no node"})))
    (do
      (log/error :do-fetch!/block-not-found {})
      {:status  :failed
       :message "No block"})))

(defn do-search!
  [props]
  (log/info :tx/searching {:props props})
  (let [{hash    ::m.c.blocks/block
         node-id ::m.c.nodes/id} props]
    (log/info :search/started {:hash hash :node-id node-id})
    (let [result (search! props)]
      (log/info :search/result {:result result})
      (if result
        {:status  :passed
         :tx      result
         :hash    hash
         :node-id node-id}
        {:status  :failed
         :tx      result
         :hash    hash
         :node-id node-id}))))

(defn do-delete!
  [props]
  (log/info :do-delete!/starting {:props props}))
