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

(report/defsc-report NodeTransactionsReport
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
                        ::m.c.tx/node  (fn [_this props] (u.links/ui-core-node-link props))}
   ro/form-links       {::m.c.tx/tx-id u.c.tx/CoreTxForm}
   ro/source-attribute ::m.c.tx/index
   ro/title            "Node Transactions"
   ro/row-actions      [u.c.tx/fetch-action-button u.c.tx/delete-action-button]
   ro/row-pk           m.c.tx/id
   ro/run-on-mount?    true})

(def ui-node-transactions-report (comp/factory NodeTransactionsReport))

(defsc NodeTransactionsSubPage
  [_this {:keys   [report] :as props
          node-id ::m.c.nodes/id}]
  {:query         [::m.c.nodes/id
                   {:report (comp/get-query u.c.tx/CoreTxReport)}]
   :pre-merge
   (fn [{:keys [data-tree state-map]}]
     (log/finer :NodeTransactionsSubPage/pre-merge {:data-tree data-tree})
     (let [initial             (comp/get-initial-state u.c.tx/CoreTxReport)
           report-data         (get-in state-map (comp/get-ident u.c.tx/CoreTxReport {}))
           updated-report-data (merge initial report-data)
           updated-data        (-> data-tree
                                   (assoc :transactions updated-report-data))]
       (log/finer :NodeTransactionsSubPage/merged {:updated-data updated-data :data-tree data-tree})
       updated-data))
   :initial-state {::m.c.nodes/id nil
                   :report        {}}
   :ident         (fn [] [:component/id ::NodeTransactionsSubPage])}
  (log/finer :NodeTransactionsSubPage/creating {:props props})
  (let [transaction-data (assoc-in report [:ui/parameters ::m.c.nodes/id] node-id)]
    (dom/div :.ui.segment
      (ui-node-transactions-report transaction-data))))

(def ui-node-transactions-sub-page (comp/factory NodeTransactionsSubPage))
