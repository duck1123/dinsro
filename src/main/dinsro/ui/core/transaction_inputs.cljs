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

(report/defsc-report Report
  [this props]
  {ro/columns          [m.c.tx-in/vout
                        m.c.tx-in/tx-id
                        m.c.tx-in/sequence
                        m.c.tx-in/transaction]
   ro/controls         {::refresh   u.links/refresh-control
                        ::m.c.tx/id {:type :uuid :label "TX"}}
   ro/control-layout   {:action-buttons [::refresh]
                        :inputs [[::m.c.tx/id]]}
   ro/source-attribute ::m.c.tx-in/index
   ro/title            "Inputs"
   ro/row-pk           m.c.tx-in/id
   ro/run-on-mount?    true}
  (log/info :Report/starting {:props props})
  (if override-form
    (report/render-layout this)
    (dom/div :.ui.segment
      (report/render-layout this))))

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [::m.c.tx/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.tx/id nil
                       :ui/report  {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/info :SubPage/creating {:props props})
  (ui-report report))

(def ui-sub-page (comp/factory SubPage))
