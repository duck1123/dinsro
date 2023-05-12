(ns dinsro.queries.core.tx-in
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

(def query-info
  {:ident   ::m.c.tx-in/id
   :pk      '?tx-in-id
   :clauses [[::m.c.networks/id    '?network-id]]
   :rules
   (fn [[transaction-id
         block-id
         network-id] rules]
     (->> rules
          (concat-when transaction-id
            [['?tx-in-id               ::m.c.tx-in/transaction  '?transaction-id]])
          (concat-when block-id
            [['?tx-in-id               ::m.c.tx-in/transaction  '?block-transaction-id]
             ['?block-transaction-id   ::m.c.transactions/block '?block-id]])
          (concat-when network-id
            [['?tx-in-id               ::m.c.tx-in/transaction  '?network-transaction-id]
             ['?network-transaction-id ::m.c.transactions/block '?network-block-id]
             ['?network-block-id       ::m.c.blocks/network     '?network-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn find-by-tx
  [tx-id]
  [::m.c.transactions/id => (s/coll-of ::m.c.tx-in/id)]
  (c.xtdb/query-values
   '{:find  [?tx-in-id]
     :in    [[?tx-id]]
     :where [[?tx-in-id ::m.c.tx-in/transaction ?tx-id]]}
   [tx-id]))

(>defn read-record
  [id]
  [::m.c.tx-in/id => (? ::m.c.tx-in/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.tx-in/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.tx-in/params => ::m.c.tx-in/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.tx-in/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.tx-in/item)]
  (map read-record (index-ids)))

(>defn delete!
  [id]
  [::m.c.tx-in/id => any?]
  (let [node (c.xtdb/get-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))
