(ns dinsro.ui.ln.node-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.ui.ln.transactions :as u.ln.transactions]
   [lambdaisland.glogi :as log]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.transactions :as m.ln.transactions]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.report :as report]
   [dinsro.ui.links :as u.links]))

(report/defsc-report NodeTransactionsReport
  [this props]
  {ro/columns
   [m.ln.transactions/block-hash
    m.ln.transactions/node]

   ro/control-layout {:action-buttons [::refresh]
                      :inputs         [[::m.ln.nodes/id]]}
   ro/controls
   {::m.ln.nodes/id
    {:type  :uuid
     :label "Nodes"}

    ::refresh
    {:type   :button
     :label  "Refresh"
     :action (fn [this] (control/run! this))}}
   ro/field-formatters {::m.ln.transactions/block (fn [_this props] (u.links/ui-block-link props))
                        ::m.ln.transactions/node  (fn [_this props] (u.links/ui-core-node-link props))}
   ro/form-links       {::m.ln.transactions/transactions-id u.ln.transactions/LNTransactionForm}
   ro/source-attribute ::m.ln.transactions/index
   ro/title            "Node Transactions"
   ro/row-pk           m.ln.transactions/id
   ro/run-on-mount?    true
   ro/route            "node-transactions"}
  (log/info :NodeTransactionsReport/creating {:props props})
  (report/render-layout this))

(def ui-node-transactions-report (comp/factory NodeTransactionsReport))

(defsc NodeTransactionsSubPage
  [_this {:keys   [report] :as props
          node-id ::m.ln.nodes/id}]
  {:query         [::m.ln.nodes/id
                   {:report (comp/get-query NodeTransactionsReport)}]
   :componentDidMount
   (fn [this]
     (let [props (comp/props this)]
       (log/info :NodeTransactionsSubPage/did-mount {:props props :this this})
       (report/start-report! this NodeTransactionsReport)))
   :initial-state {::m.ln.nodes/id nil
                   :report         {}}
   :ident         (fn [] [:component/id ::NodeTransactionsSubPage])}
  (log/info :NodeTransactionsSubPage/creating {:props props})
  (let [peer-data (assoc-in report [:ui/parameters ::m.ln.nodes/id] node-id)]
    (dom/div :.ui.segment
      #_(dom/code {} (dom/pre {} (pr-str props)))
      (if node-id
        (ui-node-transactions-report peer-data)
        (dom/div {} "Node ID not set")))))

(def ui-node-transactions-sub-page (comp/factory NodeTransactionsSubPage))
