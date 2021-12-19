(ns dinsro.queries.core-tx
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [xtdb.api :as xt]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core-block :as m.core-block]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.core-tx :as m.core-tx]
   [dinsro.queries.core-block :as q.core-block]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as log]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.core-tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.core-tx/id _]]}]
    (map first (xt/q db query))))

(>defn find-by-node
  [node-id]
  [::m.core-nodes/id => (s/coll-of ::m.core-tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?tx-id]
                :in    [?node-id]
                :where [[?tx-id ::m.core-tx/node ?node-id]]}]
    (map first (xt/q db query node-id))))

(>defn find-by-block
  [block-id]
  [::m.core-block/id => (s/coll-of ::m.core-tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?tx-id]
                :in    [?block-id]
                :where [[?tx-id ::m.core-tx/block ?block-id]]}]
    (map first (xt/q db query block-id))))

(>defn fetch-by-txid
  [tx-id]
  [::m.core-tx/tx-id => (? ::m.core-tx/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [?tx-id]
                :where [[?id ::m.core-tx/tx-id ?tx-id]]}]
    (ffirst (xt/q db query tx-id))))

(>defn read-record
  [id]
  [::m.core-tx/id => (? ::m.core-tx/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.core-tx/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.core-tx/params => ::m.core-tx/id]
  (let [node            (c.xtdb/main-node)
        id              (utils/uuid)
        prepared-params (-> params
                            (assoc ::m.core-tx/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.core-tx/item)]
  (map read-record (index-ids)))

(>defn register-tx
  [core-node-id block-hash block-height tx-id]
  [::m.core-nodes/id ::m.core-block/hash ::m.core-block/height ::m.core-tx/tx-id => ::m.core-tx/id]
  (log/info "registering tx")
  (if-let [id (fetch-by-txid tx-id)]
    (do
      (log/info "found")
      id)
    (do
      (log/info "not found")
      (let [block-id (q.core-block/register-block core-node-id block-hash block-height)
            params   {::m.core-tx/block    block-id
                      ::m.core-tx/tx-id    tx-id
                      ::m.core-tx/fetched? false}]
        (create-record params)))))

(>defn update-tx
  [id data]
  [::m.core-tx/id ::m.core-tx/params => ::m.core-tx/id]
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put (log/spy :info params)]])]
    (xt/await-tx node tx)
    id))

(comment
  2
  :the
  (first (index-records))

  nil)
