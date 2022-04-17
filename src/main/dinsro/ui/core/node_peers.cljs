(ns dinsro.ui.core.node-peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.ui.core.peers :as u.c.peers]
   [lambdaisland.glogi :as log]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.peers :as m.c.peers]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.form :as form]
   [dinsro.ui.links :as u.links]))

(report/defsc-report NodePeersReport
  [_this _props]
  {ro/columns [m.c.peers/addr
               m.c.peers/address-bind
               m.c.peers/subver
               m.c.peers/peer-id]
   ro/controls
   {::m.c.nodes/id
    {:type  :uuid
     :label "Nodes"}

    ::refresh
    {:type   :button
     :label  "Refresh"
     :action (fn [this] (control/run! this))}

    ::new-peer
    {:type   :button
     :label  "New Peer"
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
                 (form/create! this u.c.peers/NewCorePeerForm
                               {:initial-state {::m.c.peers/addr ""}})))}}

   ro/field-formatters {::m.c.peers/block (fn [_this props] (u.links/ui-block-link props))
                        ::m.c.peers/node  (fn [_this props] (u.links/ui-core-node-link props))}
   ro/form-links       {::m.c.peers/peers-id u.c.peers/CorePeerForm}
   ro/row-actions      [u.c.peers/delete-action-button]
   ro/source-attribute ::m.c.peers/index
   ro/title            "Node Peers"
   ro/row-pk           m.c.peers/id
   ro/run-on-mount?    true
   ro/route            "node-peers"})

(defsc NodePeersSubPage
  [_this {:keys   [report] :as props
          node-id ::m.c.nodes/id}]
  {:query         [::m.c.nodes/id
                   {:report (comp/get-query u.c.peers/CorePeersReport)}]
   :componentDidMount
   (fn [this]
     (let [props (comp/props this)]
       (log/info :NodePeersSubPage/did-mount {:props props :this this})
       (report/start-report! this NodePeersReport)))
   :initial-state {::m.c.nodes/id nil
                   :report        {}}
   :ident         (fn [] [:component/id ::NodePeersSubPage])}
  (log/info :NodePeersSubPage/creating {:props props})
  (let [peer-data (assoc-in report [:ui/parameters ::m.c.nodes/id] node-id)]
    (dom/div :.ui.segment
      (if node-id
        (u.c.peers/ui-peers-report peer-data)
        (dom/div {} "Node ID not set")))))

(def ui-node-peers-sub-page (comp/factory NodePeersSubPage))
