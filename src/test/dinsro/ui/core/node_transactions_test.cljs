(ns dinsro.ui.core.node-transactions-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [dinsro.ui.core.node-transactions :as u.c.node-transactions]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(defn NodeTransactionsSubPage-row
  ([]
   (let [node {::m.c.nodes/id   (ds/gen-key ::m.c.nodes/id)
               ::m.c.nodes/name (ds/gen-key ::m.c.nodes/name)}]
     (NodeTransactionsSubPage-row node)))
  ([node]
   {::m.c.tx/id       (ds/gen-key ::m.c.tx/id)
    ::m.c.tx/fetched? (ds/gen-key ::m.c.tx/fetched?)
    ::m.c.tx/tx-id    (ds/gen-key ::m.c.tx/tx-id)
    ::m.c.tx/node     node}))

(defn NodeTransactionsSubPage-report-data
  []
  {:foo             "bar"
   :ui/controls     []
   :ui/current-rows (map (fn [_] (NodeTransactionsSubPage-row)) (range 3))
   :ui/busy?        false
   :ui/parameters   {}
   :ui/page-count   1
   :ui/current-page 1
   :ui/cache        {}})

(defn NodeTransactionsSubPage-data
  []
  (let [initial-report-data (comp/get-initial-state u.c.node-transactions/NodeTransactionsSubPage)
        report-data         (merge initial-report-data (NodeTransactionsSubPage-report-data))]
    {::m.c.nodes/id (ds/gen-key ::m.c.nodes/id)
     :report        report-data}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard NodeTransactionsSubPage
  {::wsm/card-width 6 ::wsm/card-height 12}
  (th/fulcro-card
   u.c.node-transactions/NodeTransactionsSubPage
   NodeTransactionsSubPage-data
   {}))
