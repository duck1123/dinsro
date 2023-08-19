(ns dinsro.ui.admin.ln.nodes.remote-nodes
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
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../ui/admin/ln/nodes.cljc]]

(def index-page-key :admin-ln-nodes-show-remote-nodes)
(def model-key ::m.ln.remote-nodes/id)
(def parent-model-key ::m.ln.nodes/id)
(def parent-show-key :admin-ln-nodes-show)
(def parent-router :admin-ln-nodes)
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
   ro/controls          {parent-model-key {:type :uuid :label "Nodes"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [make-peer-action]
   ro/row-pk            m.ln.remote-nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.remote-nodes/admin-index
   ro/title             "Node Remote-Nodes"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key props
                         ::m.navlinks/id  index-page-key})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (get props parent-model-key)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin nodes show remote nodes report"))
    (u.debug/load-error props "admin nodes show remote nodes")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Remote Nodes"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-show-key
   ::m.navlinks/router        parent-router
   ::m.navlinks/required-role :admin})
