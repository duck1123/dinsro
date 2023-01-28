(ns dinsro.ui.core.network-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.c.networks/id)
(def router-key :dinsro.ui.core.networks/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.nodes/name
                        m.c.nodes/host
                        m.c.nodes/initial-block-download?
                        m.c.nodes/block-count]
   ro/controls         {::refresh      u.links/refresh-control
                        ::m.c.networks/id {:type :uuid :label "Nodes"}}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.nodes/name #(u.links/ui-core-node-link %3)}
   ro/source-attribute ::j.c.nodes/index
   ro/title            "Core Nodes"
   ro/row-actions       [(u.links/row-action-button "Fetch" ::m.c.nodes/id mu.c.nodes/fetch!)
                         (u.links/row-action-button "Delete" ::m.c.nodes/id mu.c.nodes/delete!)]
   ro/row-pk           m.c.nodes/id
   ro/run-on-mount?    true
   ro/route            "nodes"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {:ui/report {}}
   :route-segment     ["nodes"]
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
