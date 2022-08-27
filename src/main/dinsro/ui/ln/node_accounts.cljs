(ns dinsro.ui.ln.node-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def fetch-button
  {:type   :button
   :label  "Fetch"
   :action (u.links/report-action ::m.ln.nodes/id mu.ln.nodes/fetch-accounts!)})

(report/defsc-report Report
  [this props]
  {ro/columns          [m.ln.accounts/id
                        m.ln.accounts/node]

   ro/control-layout   {:action-buttons [::fetch ::refresh]
                        :inputs         [[::m.ln.nodes/id]]}
   ro/controls         {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                        ::fetch         fetch-button
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.ln.accounts/block           #(u.links/ui-block-link %2)
                        ::m.ln.accounts/transactions-id #(u.links/ui-core-tx-link %3)}
   ro/source-attribute ::m.ln.accounts/index
   ro/title            "Node Accounts"
   ro/row-pk           m.ln.accounts/id
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

(def ui-sub-page (comp/factory SubPage))
