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
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../../joins/core/nodes.cljc]]
;; [[../../../../model/core/nodes.cljc]]

(def index-page-id :admin-core-networks-show-nodes)
(def model-key ::m.c.nodes/id)
(def parent-model-key ::m.c.networks/id)
(def parent-router-id :admin-core-networks-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.core.networks/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.nodes/delete!))

(def fetch-action
  (u.buttons/row-action-button "Fetch" model-key mu.c.nodes/fetch!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.nodes/name #(u.links/ui-core-node-link %3)}
   ro/columns           [m.c.nodes/name
                         m.c.nodes/host
                         m.c.nodes/initial-block-download?
                         m.c.nodes/block-count]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh        u.links/refresh-control
                         parent-model-key {:type :uuid :label "Nodes"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "nodes"
   ro/row-actions       [fetch-action
                         delete-action]
   ro/row-pk            m.c.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.nodes/index
   ro/title             "Core Nodes"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this       props]
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
   :route-segment     ["nodes"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Core Nodes"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
