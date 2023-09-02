(ns dinsro.ui.admin.core.chains.networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.networks :as j.c.networks]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/core/networks.cljc]]
;; [[../../../model/core/networks.cljc]]

(def index-page-id :admin-core-chains-show-networks)
(def model-key ::m.c.networks/id)
(def parent-model-key ::m.c.chains/id)
(def parent-router-id :admin-core-chains-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.core.chains/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.networks/name  #(u.links/ui-admin-network-link %3)
                         ::m.c.networks/chain #(u.links/ui-admin-chain-link %2)}
   ro/columns           [m.c.networks/name
                         m.c.networks/chain]
   ro/control-layout    {:inputs         [[parent-model-key]]
                         :action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.networks/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.networks/admin-index
   ro/title             "Admin Chain Networks"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["networks"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Networks"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
