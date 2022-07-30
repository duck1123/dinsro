(ns dinsro.queries.core.nodes
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn create-record
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
  [id]
  [::m.c.nodes/id => (? ::m.c.nodes/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.nodes/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.nodes/name _]]}]
    (map first (xt/q db query))))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.nodes/item)]
  (map read-record (index-ids)))

(>defn find-id-by-name
  [name]
  [::m.c.nodes/name => (? ::m.c.nodes/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?node-id]
                :in    [?name]
                :where [[?node-id ::m.c.nodes/name ?name]]}]
    (ffirst (xt/q db query name))))

(>defn find-by-block
  [block-id]
  [::m.c.blocks/id => (? ::m.c.nodes/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?node-id]
                :in    [?block-id]
                :where [[?block-id ::m.c.blocks/node ?node-id]]}]
    (ffirst (xt/q db query block-id))))

(>defn find-by-ln-node
  [ln-node-id]
  [::m.ln.nodes/id => (? ::m.c.nodes/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?core-node-id]
                :in    [?ln-node-id]
                :where [[?ln-node-id ::m.ln.nodes/core-node ?core-node-id]]}]
    (ffirst (xt/q db query ln-node-id))))

(>defn find-by-tx
  [tx-id]
  [::m.c.tx/id => (? ::m.c.nodes/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?node-id]
                :in    [?tx-id]
                :where [[?block-id ::m.c.blocks/node ?node-id]
                        [?tx-id ::m.c.tx/block ?block-id]]}]
    (ffirst (xt/q db query tx-id))))

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
