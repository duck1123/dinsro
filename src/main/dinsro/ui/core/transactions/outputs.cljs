(ns dinsro.ui.core.transactions.outputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.tx-out :as j.c.tx-out]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.tx-out/n
                        m.c.tx-out/value
                        m.c.tx-out/hex
                        m.c.tx-out/type]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh   u.links/refresh-control
                        ::m.c.transactions/id {:type :uuid :label "TX"}}
   ro/row-pk           m.c.tx-out/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.tx-out/index
   ro/title            "Outputs"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [{:ui/report (comp/get-query Report)}]}
  ((comp/factory Report) report))
