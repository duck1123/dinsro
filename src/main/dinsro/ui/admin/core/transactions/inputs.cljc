(ns dinsro.ui.admin.core.transactions.inputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.tx-in :as j.c.tx-in]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.options.core.transactions :as o.c.transactions]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/tx_in.cljc]]
;; [[../../../../model/core/tx_out.cljc]]

(def index-page-id :admin-core-transactions-show-inputs)
(def parent-model-key o.c.transactions/id)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.tx-in/vout
                        m.c.tx-in/tx-id
                        m.c.tx-in/sequence]
   ro/control-layout   {:action-buttons [::refresh]
                        :inputs         [[parent-model-key]]}
   ro/controls         {::refresh        u.links/refresh-control
                        parent-model-key {:type :uuid :label "id"}}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.c.tx-in/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.tx-in/admin-index
   ro/title            "Inputs"})

(def ui-report (comp/factory Report))

(defsc SubSection
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {parent-model-key nil
                         o.navlinks/id  index-page-id
                         :ui/report       {}})
   :query             (fn [_props]
                        [parent-model-key
                         o.navlinks/id
                         {:ui/report (comp/get-query Report)}])}
  (log/info :SubSection/starting {:props props})
  (if (get props parent-model-key)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin transactions show inputs report"))
    (u.debug/load-error props "admin transactions show inputs")))

(def ui-subsection (comp/factory SubSection))
