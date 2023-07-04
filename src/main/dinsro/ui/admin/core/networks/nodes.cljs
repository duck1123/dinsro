(ns dinsro.ui.admin.core.networks.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../../joins/core/nodes.cljc]]
;; [[../../../../model/core/nodes.cljc]]

(def index-page-key :admin-core-networks-nodes)
(def model-key ::m.c.nodes/id)
(def parent-model-key ::m.c.networks/id)
(def router-key :dinsro.ui.core.networks/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.nodes/name #(u.links/ui-core-node-link %3)}
   ro/columns           [m.c.nodes/name
                         m.c.nodes/host
                         m.c.nodes/initial-block-download?
                         m.c.nodes/block-count]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh         u.links/refresh-control
                         ::m.c.networks/id {:type :uuid :label "Nodes"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "nodes"
   ro/row-actions       [(u.buttons/row-action-button "Fetch" model-key mu.c.nodes/fetch!)
                         (u.buttons/row-action-button "Delete" model-key mu.c.nodes/delete!)]
   ro/row-pk            m.c.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.nodes/index
   ro/title             "Core Nodes"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["nodes"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key model-key ::SubPage)}
  (ui-report report))