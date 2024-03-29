(ns dinsro.queries.core.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.specs]
   [xtdb.api :as xt]))

;; [[../../joins/core/transactions.cljc]]
;; [[../../model/core/transactions.cljc]]
;; [[../../../../test/dinsro/queries/core/transactions_test.clj]]

(def query-info
  {:ident   ::m.c.transactions/id
   :pk      '?transaction-id
   :clauses [[::m.c.networks/id '?network-id]
             [::m.c.blocks/id   '?block-id]
             [::m.c.tx-in/id    '?tx-in-id]]
   :rules
   (fn [[network-id block-id tx-in-id] rules]
     (->> rules
          (concat-when block-id
            [['?transaction-id   ::m.c.transactions/block '?block-id]])
          (concat-when network-id
            [['?network-block-id ::m.c.blocks/network       '?network-id]
             ['?transaction-id   ::m.c.transactions/block   '?network-block-id]])
          (concat-when tx-in-id
            [['?tx-in-id         ::m.c.tx-in/transaction    '?transaction-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn fetch-by-txid
  [tx-id]
  [::m.c.transactions/tx-id => (? ::m.c.transactions/id)]
  (c.xtdb/query-value
   '{:find  [?id]
     :in    [[?tx-id]]
     :where [[?id ::m.c.transactions/tx-id ?tx-id]]}
   [tx-id]))

(>defn read-record
  [id]
  [::m.c.transactions/id => (? ::m.c.transactions/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.transactions/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.transactions/params => ::m.c.transactions/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.transactions/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.transactions/item)]
  (map read-record (index-ids)))

(>defn update-tx
  [id data]
  [::m.c.transactions/id ::m.c.transactions/params => ::m.c.transactions/id]
  (let [node   (c.xtdb/get-node)
        db     (c.xtdb/get-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)
    id))

(>defn delete!
  [id]
  [::m.c.transactions/id => any?]
  (c.xtdb/delete! id))
