(ns dinsro.ui.core.transaction-outputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   [lambdaisland.glogi :as log]))

(def override-form false)

(report/defsc-report TransactionOutputsReport
  [this props]
  {ro/columns          [m.c.tx-out/n
                        m.c.tx-out/value
                        m.c.tx-out/address
                        m.c.tx-out/hex
                        m.c.tx-out/type]
   ro/controls
   {::refresh
    {:type   :button
     :label  "Refresh"
     :action (fn [this] (control/run! this))}
    ::m.c.tx/id
    {:type  :uuid
     :label "TX"}}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/source-attribute ::m.c.tx-out/index
   ro/title            "Outputs"
   ro/row-pk           m.c.tx-out/id
   ro/run-on-mount?    true}
  (log/info :TransactionOutputsReport/starting {:props props})
  (if override-form
    (report/render-layout this)
    (dom/div :.ui.segment
      (report/render-layout this))))

(def ui-transaction-outputs-report (comp/factory TransactionOutputsReport))

(defsc TransactionOutputsSubPage
  [_this {:ui/keys [report2] :as props}]
  {:query         [::m.c.tx/id
                   {:ui/report2 (comp/get-query TransactionOutputsReport)}]
   :componentDidMount
   (fn [this]
     (let [{id ::m.c.tx/id :as props} (comp/props this)]
       (log/info :TransactionOutputsSubPage/did-mount {:props props :this this})
       (report/start-report! this TransactionOutputsReport {:route-params {::m.c.tx/id id}})))
   :initial-state {::m.c.tx/id nil
                   :ui/report2 {}}
   :ident         (fn [] [:component/id ::TransactionOutputsSubPage])}
  (log/info :TransactionOutputsSubPage/creating {:props props})
  (when report2
    (ui-transaction-outputs-report report2)))

(def ui-transaction-outputs-sub-page (comp/factory TransactionOutputsSubPage))
