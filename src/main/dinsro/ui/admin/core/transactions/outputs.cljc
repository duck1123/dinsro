(ns dinsro.ui.admin.core.transactions.outputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.tx-out :as j.c.tx-out]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   [dinsro.options.core.transactions :as o.c.transactions]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/tx_out.cljc]]
;; [[../../../../model/core/tx_out.cljc]]

(def index-page-id :admin-core-transactions-show-outputs)
(def parent-model-key o.c.transactions/id)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.tx-out/n
                        m.c.tx-out/value
                        m.c.tx-out/hex
                        m.c.tx-out/type]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh        u.links/refresh-control
                        parent-model-key {:type :uuid :label "TX"}}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.c.tx-out/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.tx-out/index
   ro/title            "Outputs"})

(def ui-report (comp/factory Report))

(defsc SubSection
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {parent-model-key nil
                         o.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn [_props]
                        [parent-model-key
                         o.navlinks/id
                         {:ui/report (comp/get-query Report)}])}
  (log/info :SubPage/starting {:props props})
  (if (parent-model-key props)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin core transaction outputs report"))
    (u.debug/load-error props "admin core transaction outputs")))

(def ui-subsection (comp/factory SubSection))
