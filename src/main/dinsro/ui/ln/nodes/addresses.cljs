(ns dinsro.ui.ln.nodes.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.addresses :as j.c.addresses]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.addresses/id]
   ro/control-layout   {:action-buttons [::refresh]
                        :inputs         [[::m.ln.nodes/id]]}
   ro/controls         {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                        ::refresh       u.links/refresh-control}
   ro/row-pk           m.c.addresses/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.addresses/index
   ro/title            "Addresses"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["addresses"]}
  ((comp/factory Report) report))
