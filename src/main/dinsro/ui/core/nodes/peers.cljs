(ns dinsro.ui.core.nodes.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.peers :as j.c.peers]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.mutations.core.peers :as mu.c.peers]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.core.peers :as u.c.peers]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

;; [[../../../joins/core/peers.cljc]]
;; [[../../../model/core/peers.cljc]]

(def ident-key ::m.c.nodes/id)
(def model-key ::m.c.peers/id)
(def router-key :dinsro.ui.core.nodes/Router)

(def fetch-button
  {:type   :button
   :label  "Fetch"
   :action (u.buttons/report-action ::m.c.nodes/id mu.c.nodes/fetch!)})

(def new-button
  {:type   :button
   :label  "New"
   :action (fn [this]
             (let [props                 (comp/props this)
                   {:ui/keys [controls]} props
                   id-control            (some
                                          (fn [c]
                                            (let [{::control/keys [id]} c]
                                              (when (= id ::m.c.nodes/id)
                                                c)))

                                          controls)
                   node-id (::control/value id-control)]
               (log/info :peers/creating {:props      props
                                          :controls   controls
                                          :id-control id-control
                                          :node-id    node-id})
               (form/create! this u.c.peers/NewForm
                             {:initial-state {::m.c.peers/addr "foo"}})))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.peers/block #(u.links/ui-block-link %2)
                         ::m.c.peers/node  #(u.links/ui-core-node-link %2)}
   ro/columns           [m.c.peers/peer-id
                         m.c.peers/addr
                         m.c.peers/subver
                         m.c.peers/connection-type
                         m.c.peers/node]
   ro/control-layout    {:action-buttons [::new ::fetch ::refresh]
                         :inputs         [[::m.c.nodes/id]]}
   ro/controls          {::m.c.nodes/id {:type :uuid :label "Nodes"}
                         ::refresh      u.links/refresh-control
                         ::fetch        fetch-button
                         ::new          new-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "node-peers"
   ro/row-actions       [(u.buttons/row-action-button "Delete" ::m.c.peers/id mu.c.peers/delete!)]
   ro/row-pk            m.c.peers/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.peers/index
   ro/title             "Node Peers"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["peers"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (ui-report report))
