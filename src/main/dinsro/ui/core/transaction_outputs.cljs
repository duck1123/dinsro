(ns dinsro.ui.core.transaction-outputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def override-form false)

(report/defsc-report Report
  [this props]
  {ro/columns          [m.c.tx-out/n
                        m.c.tx-out/value
                        m.c.tx-out/address
                        m.c.tx-out/hex
                        m.c.tx-out/type]
   ro/controls         {::refresh   u.links/refresh-control
                        ::m.c.tx/id {:type :uuid :label "TX"}}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/source-attribute ::m.c.tx-out/index
   ro/title            "Outputs"
   ro/row-pk           m.c.tx-out/id
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
                       :ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/info :SubPage/creating {:props props})
  (when report
    (ui-report report)))

(def ui-sub-page (comp/factory SubPage))
