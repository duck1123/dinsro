(ns dinsro.queries.core.networks
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb  :refer [concat-when]]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.c.networks/id
   :pk      '?network-id
   :clauses [[::m.c.chains/id    '?chain-id]
             [::m.c.networks/name '?network-name]]
   :rules
   (fn [[chain-id network-name] rules]
     (->> rules
          (concat-when chain-id
            [['?network-id ::m.c.networks/chain '?chain-id]])
          (concat-when network-name
            [['?network-id ::m.c.networks/name '?network-name]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.c.networks/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.networks/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [::m.c.networks/id => (? ::m.c.networks/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.networks/id)
      (dissoc record :xt/id))))

(>defn find-by-node-id
  "Returns the id of the network the node with the provided id belongs to."
  [node-id]
  [::m.c.nodes/id => (? ::m.c.networks/id)]
  (log/trace :find-by-node/starting {:node-id node-id})
  (c.xtdb/query-value
   '{:find  [?network-id]
     :in    [[?node-id]]
     :where [[?node-id ::m.c.nodes/network ?network-id]]}
   [node-id]))

(>defn find-by-chain-and-network
  [chain-name network-name]
  [::m.c.chains/name ::m.c.networks/name => (? ::m.c.networks/id)]
  (log/trace :find-by-chain-and-network/starting {:chain-name chain-name :network-name network-name})
  (c.xtdb/query-value
   '{:find  [?network-id]
     :in    [[?chain-name ?network-name]]
     :where [[?chain-id ::m.c.chains/name ?chain-name]
             [?network-id ::m.c.networks/chain ?chain-id]
             [?network-id ::m.c.networks/name ?network-name]]}
   [chain-name network-name]))

(>defn delete!
  [id]
  [::m.c.networks/id => any?]
  (let [node (c.xtdb/get-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(>defn find-by-core-node
  [core-node-id]
  [::m.c.nodes/id => (? ::m.c.networks/id)]
  (log/trace :find-by-core-node/starting {:core-node-id core-node-id})
  (c.xtdb/query-value
   '{:find  [?network-id]
     :in    [[?core-node-id]]
     :where [[?core-node-id ::m.c.nodes/network ?network-id]]}
   [core-node-id]))
