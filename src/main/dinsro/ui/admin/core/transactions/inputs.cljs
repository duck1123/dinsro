(ns dinsro.ui.admin.core.transactions.inputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.tx-in :as j.c.tx-in]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/tx_in.cljc]]
;; [[../../../../model/core/tx_out.cljc]]

(def index-page-key :admin-core-transactions-show-inputs)
(def model-key ::m.c.tx-in/id)
(def parent-model-key ::m.c.transactions/id)

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

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.c.transactions/keys [id]
          :ui/keys                [report]
          :as                     props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.c.transactions/id nil
                       ::m.navlinks/id       index-page-key
                       :ui/report            {}}
   :query             [::m.c.transactions/id
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))

(def ui-subpage (comp/factory SubPage))
