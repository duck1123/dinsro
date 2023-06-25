(ns dinsro.ui.admin.core.transactions.outputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.tx-out :as j.c.tx-out]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]))

;; [[../../../../joins/core/tx_out.cljc]]
;; [[../../../../model/core/tx_out.cljc]]

(def index-page-key :admin-core-transactions-outputs)
(def model-key ::m.c.tx-out/id)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.tx-out/n
                        m.c.tx-out/value
                        m.c.tx-out/hex
                        m.c.tx-out/type]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh             u.links/refresh-control
                        ::m.c.transactions/id {:type :uuid :label "TX"}}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.c.tx-out/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.tx-out/index
   ro/title            "Outputs"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]}
  (ui-report report))
