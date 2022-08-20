(ns dinsro.ui.ln.node-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.transactions :as m.ln.transactions]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.transactions :as u.ln.transactions]
   [lambdaisland.glogi :as log]))

(report/defsc-report Report
  [this props]
  {ro/columns          [m.ln.transactions/block-hash
                        m.ln.transactions/node]
   ro/control-layout   {:action-buttons [::refresh]
                        :inputs         [[::m.ln.nodes/id]]}
   ro/controls         {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.ln.transactions/block #(u.links/ui-block-link %2)
                        ::m.ln.transactions/node  #(u.links/ui-core-node-link %2)}
   ro/form-links       {::m.ln.transactions/transactions-id u.ln.transactions/LNTransactionForm}
   ro/source-attribute ::m.ln.transactions/index
   ro/title            "Node Transactions"
   ro/row-pk           m.ln.transactions/id
   ro/run-on-mount?    true
   ro/route            "node-transactions"}
  (log/info :Report/creating {:props props})
  (report/render-layout this))

(def ui-node-transactions-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props
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
      (ui-node-transactions-report report)
      (dom/div {} "Node ID not set"))))

(def ui-node-transactions-sub-page (comp/factory SubPage))
