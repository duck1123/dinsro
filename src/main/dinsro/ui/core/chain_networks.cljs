(ns dinsro.ui.core.chain-networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.c.chains/id)
(def router-key :dinsro.ui.core.chains/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.networks/name]
   ro/control-layout   {:inputs         [[::m.c.chains/id]]
                        :action-buttons [::refresh]}
   ro/controls         {::m.c.chains/id {:type :uuid :label "Chains"}
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.c.networks/name #(u.links/ui-network-link %3)}
   ro/source-attribute ::m.c.networks/index
   ro/title            "Chain Networks"
   ro/row-pk           m.c.networks/id
   ro/run-on-mount?    true})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["remote-nodes"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
