(ns dinsro.ui.core.node-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.mutations.core.transactions :as mu.c.transactions]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.c.nodes/id)
(def router-key :dinsro.ui.core.nodes/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.transactions/tx-id
                        j.c.transactions/node
                        m.c.transactions/fetched?
                        m.c.transactions/block]
   ro/controls         {::m.c.nodes/id {:type :uuid :label "id"}
                        ::refresh      u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.transactions/block #(u.links/ui-block-height-link %2)
                        ::m.c.transactions/node  #(u.links/ui-core-node-link %2)
                        ::m.c.tx-id    #(u.links/ui-core-tx-link %3)}
   ro/source-attribute ::j.c.transactions/index
   ro/title            "Node Transactions"
   ro/row-actions      [(u.links/row-action-button "Fetch" ::m.c.transactions/id mu.c.transactions/fetch!)
                        (u.links/row-action-button "Delete" ::m.c.transactions/id mu.c.transactions/delete!)]
   ro/row-pk           m.c.transactions/id
   ro/run-on-mount?    true})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["transactions"]}
  ((comp/factory Report) report))
