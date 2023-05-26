(ns dinsro.processors.core.transactions
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.core.transactions :as a.c.transactions]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.transactions :as q.c.tx]
   [dinsro.responses.core.transactions :as r.c.transactions]
   [lambdaisland.glogc :as log]))

;; [../../actions/core/transactions.clj]
;; [../../model/core/transactions.cljc]
;; [../../mutations/core/transactions.cljc]
;; [../../responses/core/transactions.cljc]

(defn search!
  [props]
  (log/info :do-search!/starting {:props props})
  (let [{tx-id   ::m.c.transactions/tx-id
         node-id ::m.c.transactions/node} props]
    (log/info :search/started {:tx-id tx-id :node-id node-id})
    (let [result (a.c.transactions/search! props)]
      (log/info :search/result {:result result})
      (if result
        {:status  :passed
         :tx      result
         :tx-id   tx-id
         :node-id node-id}
        {:status  :failed
         :tx      result
         :tx-id   tx-id
         :node-id node-id}))))

(defn delete!
  [props]
  (log/info :delete!/starting {:props props}))

(>defn fetch!
  "Fetch tx info from node. Mutation handler"
  [{::m.c.transactions/keys [id]}]
  [::m.c.transactions/id-obj => ::r.c.transactions/fetch-result]
  (if-let [tx (q.c.tx/read-record id)]
    (if-let [block-id (::m.c.transactions/block tx)]
      (if-let [block (q.c.blocks/read-record block-id)]
        (if-let [network-id (::m.c.blocks/network block)]
          (if-let [node-id (first (q.c.nodes/find-by-network network-id))]
            (let [{::m.c.transactions/keys [tx-id]} tx
                  returned-id                       (a.c.transactions/update-tx node-id block-id tx-id)]
              {:status                 :passed
               ::m.c.transactions/item (q.c.tx/read-record returned-id)
               :id                     returned-id})
            (do
              (log/warn :fetch!/no-node-id {:network-id network-id})
              {:status :failed}))
          (do
            (log/warn :fetch!/no-network-id {:block block})
            {:status :failed}))
        (do
          (log/info :fetch!/block-not-found {:block-id block-id})
          {:status :failed}))
      (do
        (log/warn :fetch!/no-block-id {:id id :tx tx})
        {:status :failed}))
    (do
      (log/warn :fetch!/tx-not-read {:id id})
      {:status :failed})))
