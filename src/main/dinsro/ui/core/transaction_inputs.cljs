(ns dinsro.ui.core.transaction-inputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def override-form false)

(report/defsc-report TransactionInputsReport
  [this props]
  {ro/columns          [m.c.tx-in/txid
                        m.c.tx-in/vout
                        m.c.tx-in/sequence
                        m.c.tx-in/coinbase]
   ro/controls         {::refresh   u.links/refresh-control
                        ::m.c.tx/id {:type :uuid :label "TX"}}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/source-attribute ::m.c.tx-in/index
   ro/title            "Inputs"
   ro/row-pk           m.c.tx-in/id
   ro/run-on-mount?    true}
  (log/info :TransactionInputsReport/starting {:props props})
  (if override-form
    (report/render-layout this)
    (dom/div :.ui.segment
      (report/render-layout this))))

(def ui-transaction-inputs-report (comp/factory TransactionInputsReport))

(defsc TransactionInputsSubPage
  [_this {:ui/keys [report] :as props}]
  {:query         [::m.c.tx/id
                   {:ui/report (comp/get-query TransactionInputsReport)}]
   :componentDidMount
   (fn [this]
     (let [{id ::m.c.tx/id :as props} (comp/props this)]
       (log/info :TransactionInputsSubPage/did-mount {:props props :this this})
       (report/start-report! this TransactionInputsReport {:route-params {::m.c.tx/id id}})))
   :initial-state {::m.c.tx/id nil
                   :ui/report      {}}
   :ident         (fn [] [:component/id ::TransactionInputsSubPage])}
  (log/info :TransactionInputsSubPage/creating {:props props})
  (ui-transaction-inputs-report report))

(def ui-transaction-inputs-sub-page (comp/factory TransactionInputsSubPage))
