(ns dinsro.ui.admin.core.nodes.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.transactions :as mu.c.transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]))

;; [[../../../../joins/core/transactions.cljc]]
;; [[../../../../model/core/transactions.cljc]]

(def ident-key ::m.c.nodes/id)
(def index-page-key :core-nodes-transactions)
(def model-key ::m.c.transactions/id)
(def router-key :dinsro.ui.core.nodes/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.transactions/block #(u.links/ui-block-height-link %2)
                         ::m.c.transactions/node  #(u.links/ui-core-node-link %2)
                         ::m.c.tx-id              #(u.links/ui-core-tx-link %3)}
   ro/columns           [m.c.transactions/tx-id
                         j.c.transactions/node
                         m.c.transactions/fetched?
                         m.c.transactions/block]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {ident-key {:type :uuid :label "id"}
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Fetch" model-key mu.c.transactions/fetch!)
                         (u.buttons/row-action-button "Delete" model-key mu.c.transactions/delete!)]
   ro/row-pk            m.c.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.transactions/index
   ro/title             "Node Transactions"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id ::SubPage])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["transactions"]}
  (ui-report report))
