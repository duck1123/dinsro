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
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-key :settings-ln-nodes-show-remote-nodes)
(def model-key ::m.ln.remote-nodes/id)
(def parent-model-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

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
   ro/row-actions       [(u.buttons/subrow-action-button "Make Peer" ::m.ln.remote-nodes/id ::m.ln.nodes/id mu.ln.nodes/make-peer!)]
   ro/row-pk            m.ln.remote-nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.remote-nodes/index
   ro/title             "Node Remote-Nodes"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["remote-nodes"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (ui-report report))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Remote Nodes"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :settings-ln-nodes-show
   ::m.navlinks/router        :settings-ln-nodes
   ::m.navlinks/required-role :user})
