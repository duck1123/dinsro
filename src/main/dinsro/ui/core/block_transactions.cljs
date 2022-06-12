(ns dinsro.ui.core.block-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.ui.core.tx :as u.c.tx]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def override-form false)

(report/defsc-report BlockTransactionsReport
  [this props]
  {ro/columns          [m.c.tx/tx-id
                        m.c.tx/fetched?
                        m.c.tx/block]
   ro/controls
   {::refresh
    {:type   :button
     :label  "Refresh"
     :action (fn [this] (control/run! this))}
    ::m.c.blocks/id
    {:type  :uuid
     :label "Block"}}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.tx/block #(u.links/ui-block-height-link %2)
                        ::m.c.tx/tx-id (u.links/report-link ::m.c.tx/tx-id u.links/ui-core-tx-link)}
   ;; ro/form-links       {::m.c.tx/tx-id u.c.tx/CoreTxForm}
   ro/source-attribute ::m.c.tx/index
   ro/title            "Transactions"
   ro/row-actions      [u.c.tx/fetch-action-button u.c.tx/delete-action-button]
   ro/row-pk           m.c.tx/id
   ro/run-on-mount?    true}
  (log/info :BlockTransactionsReport/starting {:props props})
  (if override-form
    (report/render-layout this)
    (dom/div :.ui.segment
      (report/render-layout this))))

(def ui-block-transactions-report (comp/factory BlockTransactionsReport))

(defsc BlockTransactionsSubPage
  [_this {:ui/keys [report] :as props}]
  {:query         [::m.c.blocks/id
                   {:ui/report (comp/get-query BlockTransactionsReport)}]
   :componentDidMount
   (fn [this]
     (let [props    (comp/props this)
           block-id (::m.c.blocks/id props)]
       (log/info :BlockTransactionsSubPage/did-mount {:props props :this this})
       (report/start-report! this BlockTransactionsReport {:route-params {::m.c.blocks/id block-id}})))
   :initial-state {::m.c.blocks/id nil
                   :ui/report      {}}
   :ident         (fn [] [:component/id ::BlockTransactionsSubPage])}
  (log/info :BlockTransactionsSubPage/creating {:props props})
  (ui-block-transactions-report report))

(def ui-block-transactions-sub-page (comp/factory BlockTransactionsSubPage))
