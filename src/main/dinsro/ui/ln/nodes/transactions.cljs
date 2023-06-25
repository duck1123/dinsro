(ns dinsro.ui.ln.nodes.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/core/transactions.cljc]]
;; [[../../../model/core/transactions.cljc]]

(def ident-key ::m.c.transactions/id)
(def index-page-key :ln-nodes-transactions)
(def model-key ::m.c.transactions/id)
(def parent-model-key ::m.c.transactions/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.transactions/block #(u.links/ui-block-link %2)
                         ::m.c.transactions/tx-id #(u.links/ui-core-tx-link %3)}
   ro/columns           [m.c.transactions/block-hash]
   ro/control-layout    {:action-buttons [::fetch ::refresh]
                         :inputs         [[::m.ln.nodes/id]]}
   ro/controls          {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                         ::fetch         {:type   :button
                                          :label  "Fetch"
                                          :action (u.buttons/report-action ::m.ln.nodes/id mu.ln.nodes/fetch-transactions!)}
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.transactions/index
   ro/title             "Node Transactions"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["transactions"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (ui-report report))
