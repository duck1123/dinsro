(ns dinsro.ui.ln.node-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def fetch-button
  {:type   :button
   :label  "Fetch"
   :action (u.links/report-action ::m.ln.nodes/id mu.ln.nodes/fetch-transactions!)})

(report/defsc-report Report
  [this props]
  {ro/columns          [m.c.tx/block-hash]
   ro/control-layout   {:action-buttons [::fetch ::refresh]
                        :inputs         [[::m.ln.nodes/id]]}
   ro/controls         {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                        ::fetch         fetch-button
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.c.tx/block #(u.links/ui-block-link %2)
                        ::m.c.tx/tx-id #(u.links/ui-core-tx-link %3)}
   ro/source-attribute ::m.c.tx/index
   ro/title            "Node Transactions"
   ro/row-pk           m.c.tx/id
   ro/run-on-mount?    true}
  (log/info :Report/creating {:props props})
  (report/render-layout this))

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props
          node-id  ::m.ln.nodes/id}]
  {:query             [::m.ln.nodes/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.ln.nodes/id nil
                       :ui/report      {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/info :SubPage/creating {:props props})
  (dom/div :.ui.segment
    (if node-id
      (ui-report report)
      (dom/div {} "Node ID not set"))))
