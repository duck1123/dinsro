(ns dinsro.ui.core.network-ln-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.ui.links :as u.links]))

(def router-key :dinsro.ui.core.networks/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.nodes/name
                        m.ln.nodes/user]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::m.c.networks/id {:type :uuid :label "Network"}
                        ::refresh         u.links/refresh-control}
   ro/field-formatters {::m.ln.nodes/name #(u.links/ui-node-link %3)
                        ::m.ln.nodes/user #(u.links/ui-user-link %2)}
   ro/row-pk           m.ln.nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.nodes/index
   ro/title            "Lightning Nodes"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:ident         (fn [] [:component/id ::SubPage])
   :initial-state {:ui/report {}}
   :query         [[::dr/id router-key]
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["ln-nodes"]}
  ((comp/factory Report) report))
