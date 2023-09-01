(ns dinsro.mocks.ui.core.nodes.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.specs :as ds]
   [dinsro.ui.core.nodes.transactions :as u.c.n.transactions]))

;; [[../../../../ui/core/nodes/transactions.cljc]]
;; [[../../../../../../test/dinsro/ui/core/nodes/transactions_test.cljs]]

(defn SubPage-row
  ([]
   (let [node {::m.c.nodes/id   (ds/gen-key ::m.c.nodes/id)
               ::m.c.nodes/name (ds/gen-key ::m.c.nodes/name)}]
     (SubPage-row node)))
  ([node]
   {::m.c.transactions/id       (ds/gen-key ::m.c.transactions/id)
    ::m.c.transactions/fetched? (ds/gen-key ::m.c.transactions/fetched?)
    ::m.c.transactions/tx-id    (ds/gen-key ::m.c.transactions/tx-id)
    ::m.c.transactions/node     node}))

(defn Report-data
  []
  {:foo             "bar"
   :ui/controls     []
   :ui/current-rows (map (fn [_] (SubPage-row)) (range 3))
   :ui/busy?        false
   :ui/parameters   {}
   :ui/page-count   1
   :ui/current-page 1
   :ui/cache        {}})

(defn SubPage-data
  []
  (let [initial-report-data (comp/get-initial-state u.c.n.transactions/SubPage)
        report-data         (merge initial-report-data (Report-data))]
    {::m.c.nodes/id  (ds/gen-key ::m.c.nodes/id)
     ::m.navlinks/id u.c.n.transactions/index-page-id
     :ui/report      report-data}))
