(ns dinsro.ui.settings.ln.nodes.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.peers :as j.ln.peers]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.mutations.ln.peers :as mu.ln.peers]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def index-page-id :settings-ln-nodes-show-peers)
(def model-key ::m.ln.peers/id)
(def parent-model-key ::m.ln.nodes/id)
(def parent-router-id :settings-ln-nodes-show)
(def required-role :user)
(def router-key :dinsro.ui.ln.nodes/Router)

(def delete-action
  (u.buttons/subrow-action-button "Delete" model-key parent-model-key mu.ln.peers/delete!))

(def fetch-button
  {:type   :button
   :label  "Fetch"
   :action (u.buttons/report-action ::m.ln.nodes/id mu.ln.nodes/fetch-peers!)})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.peers/block       #(u.links/ui-block-link %2)
                         ::m.ln.peers/node        #(u.links/ui-core-node-link %2)
                         ::m.ln.peers/remote-node #(u.links/ui-remote-node-link %2)}
   ro/columns           [m.ln.peers/remote-node
                         m.ln.peers/sat-recv
                         m.ln.peers/sat-sent
                         m.ln.peers/inbound?]
   ro/control-layout    {:action-buttons [::fetch ::new ::refresh]
                         :inputs         [[::m.ln.nodes/id]]}
   ro/controls          {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                         ::fetch         fetch-button
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.ln.peers/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.peers/index
   ro/title             "Node Peers"})

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
   :route-segment     ["peers"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/label         "Peers"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
