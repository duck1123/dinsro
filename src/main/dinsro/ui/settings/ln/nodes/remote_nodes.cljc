(ns dinsro.ui.settings.ln.nodes.remote-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.remote-nodes :as j.ln.remote-nodes]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def index-page-id :settings-ln-nodes-show-remote-nodes)
(def model-key ::m.ln.remote-nodes/id)
(def parent-model-key ::m.ln.nodes/id)
(def parent-router-id :settings-ln-nodes-show)
(def required-role :user)
(def router-key :dinsro.ui.ln.nodes/Router)

(def make-peer-action
  (u.buttons/subrow-action-button "Make Peer" model-key parent-model-key mu.ln.nodes/make-peer!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.remote-nodes/block #(u.links/ui-block-link %2)
                         ::m.ln.remote-nodes/node  #(when %2 (u.links/ui-core-node-link %2))}
   ro/columns           [m.ln.remote-nodes/pubkey
                         m.ln.remote-nodes/host
                         m.ln.remote-nodes/alias
                         m.ln.remote-nodes/color
                         m.ln.remote-nodes/node]
   ro/control-layout    {:action-buttons [::refresh]
                         :inputs         [[::m.ln.nodes/id]]}
   ro/controls          {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [make-peer-action]
   ro/row-pk            m.ln.remote-nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.remote-nodes/index
   ro/title             "Node Remote-Nodes"})

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
   :route-segment     ["remote-nodes"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Remote Nodes"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
