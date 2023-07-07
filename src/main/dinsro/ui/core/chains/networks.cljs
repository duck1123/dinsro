(ns dinsro.ui.core.chains.networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.networks :as j.c.networks]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/core/networks.cljc]]
;; [[../../../model/core/networks.cljc]]

(def index-page-key :core-chains-networks)
(def model-key ::m.c.networks/id)
(def parent-model-key ::m.c.chains/id)
(def router-key :dinsro.ui.core.chains/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.networks/name  #(u.links/ui-network-link %3)
                         ::m.c.networks/chain #(u.links/ui-chain-link %2)}
   ro/columns           [m.c.networks/name
                         m.c.networks/chain]
   ro/control-layout    {:inputs         [[::m.c.chains/id]]
                         :action-buttons [::refresh]}
   ro/controls          {::m.c.chains/id {:type :uuid :label "Chains"}
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.networks/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.networks/index
   ro/title             "Chain Networks"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["networks"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (if report
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))
