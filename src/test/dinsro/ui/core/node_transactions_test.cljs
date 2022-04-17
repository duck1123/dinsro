(ns dinsro.ui.core.node-transactions-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.client :as client]
   [dinsro.specs :as ds]
   [dinsro.ui.core.node-transactions :as u.c.node-transactions]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
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
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.node-transactions/NodeTransactionsSubPage
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] (NodeTransactionsSubPage-data))}))
