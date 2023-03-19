(ns dinsro.ui.ln.nodes.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def ident-key ::m.c.transactions/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(report/defsc-report Report
  [this props]
  {ro/columns          [m.c.transactions/block-hash]
   ro/control-layout   {:action-buttons [::fetch ::refresh]
                        :inputs         [[::m.ln.nodes/id]]}
   ro/controls         {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                        ::fetch         {:type   :button
                                         :label  "Fetch"
                                         :action (u.links/report-action ::m.ln.nodes/id mu.ln.nodes/fetch-transactions!)}
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.c.transactions/block #(u.links/ui-block-link %2)
                        ::m.c.transactions/tx-id #(u.links/ui-core-tx-link %3)}
   ro/source-attribute ::j.c.transactions/index
   ro/title            "Node Transactions"
   ro/row-pk           m.c.transactions/id
   ro/run-on-mount?    true}
  (log/info :Report/creating {:props props})
  (report/render-layout this))

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report      {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["transactions"]}
  ((comp/factory Report) report))
