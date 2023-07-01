(ns dinsro.ui.ln.nodes.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.peers :as j.ln.peers]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.mutations.ln.peers :as mu.ln.peers]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/ln/peers.cljc]]
;; [[../../../model/ln/peers.cljc]]

(def ident-key ::m.ln.nodes/id)
(def model-key ::m.ln.peers/id)
(def router-key :dinsro.ui.ln.nodes/Router)

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
   ro/row-actions       [(u.buttons/subrow-action-button "Delete" ::m.ln.peers/id ident-key mu.ln.peers/delete!)]
   ro/row-pk            m.ln.peers/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.peers/index
   ro/title             "Node Peers"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["peers"]}
  (ui-report report))
