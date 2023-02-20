(ns dinsro.queries.core.nodes
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn create-record
  "Create a node record"
  [params]
  [::m.c.nodes/params => :xt/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.nodes/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  "Read a node record"
  [id]
  [::m.c.nodes/id => (? ::m.c.nodes/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.nodes/id)
      (dissoc record :xt/id))))

(>defn index-ids
  "Return the id of every node"
  []
  [=> (s/coll-of :xt/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.nodes/name _]]}]
    (map first (xt/q db query))))

(>defn index-records
  "Read all node records"
  []
  [=> (s/coll-of ::m.c.nodes/item)]
  (map read-record (index-ids)))

(>defn find-by-name
  [name]
  [::m.c.nodes/name => (? ::m.c.nodes/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?node-id]
                :in    [?name]
                :where [[?node-id ::m.c.nodes/name ?name]]}]
    (ffirst (xt/q db query name))))

(>defn find-by-network
  "Find all nodes associated with a network"
  [network-id]
  [::m.c.networks/id => (s/coll-of ::m.c.nodes/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?node-id]
                :in    [[?network-id]]
                :where [[?node-id ::m.c.nodes/network ?network-id]]}]
    (map first (xt/q db query [network-id]))))

(>defn find-by-ln-node
  [ln-node-id]
  [::m.ln.nodes/id => (? ::m.c.nodes/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?core-node-id]
                :in    [?ln-node-id]
                :where [[?ln-node-id ::m.ln.nodes/core-node ?core-node-id]]}]
    (ffirst (xt/q db query ln-node-id))))

(>defn update-blockchain-info
  [id props]
  [::m.c.nodes/id ::m.c.nodes/item => any?]
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge  old props)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))))

(defn update-wallet-info
  [{:keys            [balance tx-count]
    ::m.c.nodes/keys [id]
    :as              params}]
  (log/info :update-wallet-info {:params params})
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge
                old
                {:wallet-info/balance  balance
                 :wallet-info/tx-count tx-count})]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))))

(>defn delete!
  [id]
  [::m.c.nodes/id => any?]
  (let [node (c.xtdb/main-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(defn find-by-tx
  [tx-id]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?node-id]
                :in    [[?tx-id]]
                :where [[?tx-id ::m.c.transactions/block ?block-id]
                        [?block-id ::m.c.blocks/network ?network-id]
                        [?node-id ::m.c.nodes/network ?network-id]]}]
    (ffirst (xt/q db query [tx-id]))))

(defn find-by-user
  [_user-id]
  (index-ids))
