(ns dinsro.ui.core.transactions.inputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.tx-in :as j.c.tx-in]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.tx-in/vout
                        m.c.tx-in/tx-id
                        m.c.tx-in/sequence]
   ro/control-layout   {:action-buttons [::refresh]
                        :inputs         [[::m.c.transactions/id]]}
   ro/controls         {::refresh             u.links/refresh-control
                        ::m.c.transactions/id {:type :uuid :label "TX"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk           m.c.tx-in/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.tx-in/index
   ro/title            "Inputs"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {::m.c.transactions/id nil
                       :ui/report            {}}
   :query             [::m.c.transactions/id
                       {:ui/report (comp/get-query Report)}]}
  ((comp/factory Report) report))
