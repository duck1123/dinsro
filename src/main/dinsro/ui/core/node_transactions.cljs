(ns dinsro.ui.core.node-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.tx :as j.c.tx]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.ui.core.tx :as u.c.tx]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.tx/tx-id
                        j.c.tx/node
                        m.c.tx/fetched?
                        m.c.tx/block]
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.tx/block (fn [_this props]
                                         (log/debug :formatting {:props props})
                                         (u.links/ui-block-height-link props))
                        ::m.c.tx/node  #(u.links/ui-core-node-link %2)}
   ro/form-links       {::m.c.tx/tx-id u.c.tx/CoreTxForm}
   ro/source-attribute ::m.c.tx/index
   ro/title            "Node Transactions"
   ro/row-actions      [u.c.tx/fetch-action-button u.c.tx/delete-action-button]
   ro/row-pk           m.c.tx/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props
          node-id  ::m.c.nodes/id}]
  {:query         [::m.c.nodes/id
                   {:ui/report (comp/get-query Report)}]
   :initial-state {::m.c.nodes/id nil
                   :ui/report     {}}
   :ident         (fn [] [:component/id ::SubPage])}
  (log/finer :SubPage/creating {:props props})
  (let [transaction-data (assoc-in report [:ui/parameters ::m.c.nodes/id] node-id)]
    (dom/div :.ui.segment
      (ui-report transaction-data))))

(def ui-sub-page (comp/factory SubPage))
