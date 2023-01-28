(ns dinsro.ui.core.network-ln-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.nodes/name
                        m.ln.nodes/user]
   ro/controls         {::refresh         u.links/refresh-control
                        ::m.c.networks/id {:type :uuid :label "Network"}}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.ln.nodes/name #(u.links/ui-node-link %3)
                        ::m.ln.nodes/user #(u.links/ui-user-link %2)}
   ro/source-attribute ::j.ln.nodes/index
   ro/title            "Lightning Nodes"
   ro/row-pk           m.ln.nodes/id
   ro/run-on-mount?    true})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query         [[::dr/id :dinsro.ui.core.networks/Router]
                   {:ui/report (comp/get-query Report)}]
   :initial-state {:ui/report {}}
   :route-segment ["ln-nodes"]
   :ident         (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))

