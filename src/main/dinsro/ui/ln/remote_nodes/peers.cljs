(ns dinsro.ui.ln.remote-nodes.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.peers :as j.ln.peers]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/ln/peers.cljc]]
;; [[../../../model/ln/peers.cljc]]

(def index-page-key :ln-remote-nodes-show-peers)
(def model-key ::m.ln.peers/id)
(def parent-model-key ::m.ln.remote-nodes/id)
(def router-key :dinsro.ui.ln.remote-nodes/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.peers/block       #(u.links/ui-block-link %2)
                         ::m.ln.peers/node        #(u.links/ui-core-node-link %2)
                         ::m.ln.peers/remote-node #(u.links/ui-remote-node-link %2)}
   ro/columns           [m.ln.peers/remote-node
                         m.ln.peers/sat-recv
                         m.ln.peers/sat-sent
                         m.ln.peers/inbound?]
   ro/control-layout    {:action-buttons [::refresh]
                         :inputs         [[::m.ln.remote-nodes/id]]}
   ro/controls          {::m.ln.remote-nodes/id {:type :uuid :label "Nodes"}
                         ::refresh              u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/source-attribute  ::j.ln.peers/index
   ro/title             "Remote Node Peers"
   ro/row-pk            m.ln.peers/id
   ro/run-on-mount?     true})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (ui-report report))

(def ui-sub-page (comp/factory SubPage))
