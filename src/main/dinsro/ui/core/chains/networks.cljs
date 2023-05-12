(ns dinsro.ui.core.chains.networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.networks :as j.c.networks]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.c.chains/id)
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

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["networks"]}
  ((comp/factory Report) report))
