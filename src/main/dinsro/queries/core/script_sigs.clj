(ns dinsro.queries.core.script-sigs
  (:require
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.script-sigs :as m.c.script-sigs]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.specs]))

(def query-info
  {:ident   ::m.c.script-sigs/id
   :pk      '?script-sigs-id
   :clauses [[::m.c.tx-in/id        '?tx-in-id]
             [::m.c.transactions/id '?transaction-id]
             [::m.c.blocks/id       '?block-id]
             [::m.c.networks/id     '?network-id]]
   :rules
   (fn [[tx-in-id
         transaction-id
         block-id
         network-id] rules]
     (->> rules
          (concat-when tx-in-id
            [['?script-sigs-id         ::m.c.script-sigs/tx-in  '?tx-in-id]])
          (concat-when transaction-id
            [['?script-sigs-id         ::m.c.script-sigs/tx-in  '?transaction-tx-in-id]
             ['?transaction-tx-in-id   ::m.c.tx-in/transaction  '?transaction-id]])
          (concat-when block-id
            [['?script-sigs-id         ::m.c.script-sigs/tx-in  '?block-tx-in-id]
             ['?block-tx-in-id         ::m.c.tx-in/transaction  '?block-transaction-id]
             ['?block-transaction-id   ::m.c.transactions/block '?block-id]])
          (concat-when network-id
            [['?script-sigs-id         ::m.c.script-sigs/tx-in  '?network-tx-in-id]
             ['?network-tx-in-id       ::m.c.tx-in/transaction  '?network-transaction-id]
             ['?network-transaction-id ::m.c.transactions/block '?network-block-id]
             ['?network-block-id       ::m.c.blocks/network     '?network-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))
