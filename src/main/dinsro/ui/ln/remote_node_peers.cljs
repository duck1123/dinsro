(ns dinsro.ui.ln.remote-node-peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.form :as form]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.peers :as u.ln.peers]
   [lambdaisland.glogi :as log]))

(report/defsc-report RemoteNodePeersReport
  [this props]
  {ro/columns
   [m.ln.peers/address
    m.ln.peers/remote-node
    m.ln.peers/sat-recv
    m.ln.peers/sat-sent
    m.ln.peers/inbound]

   ro/control-layout {:action-buttons [::new ::refresh]
                      :inputs         [[::m.ln.remote-nodes/id]]}
   ro/controls
   {::m.ln.nodes/id
    {:type  :uuid
     :label "Nodes"}

    ::refresh
    {:type   :button
     :label  "Refresh"
     :action (fn [this] (control/run! this))}

    ::new
    {:type   :button
     :label  "New"
     :action (fn [this]
               (let [props                 (comp/props this)
                     {:ui/keys [controls]} props
                     id-control            (some
                                            (fn [c]
                                              (let [{::control/keys [id]} c]
                                                (when (= id ::m.ln.nodes/id)
                                                  c)))

                                            controls)
                     node-id (::control/value id-control)]
                 (log/info :peers/creating {:props      props
                                            :controls   controls
                                            :id-control id-control
                                            :node-id    node-id})
                 (form/create! this u.ln.peers/NewPeerForm
                               {:initial-state {::m.ln.peers/address "foo"}})))}}

   ro/field-formatters
   {::m.ln.peers/block       (fn [_this props] (u.links/ui-block-link props))
    ::m.ln.peers/node        (fn [_this props] (u.links/ui-core-node-link props))
    ::m.ln.peers/remote-node (fn [_this props] (u.links/ui-remote-node-link props))}
   ro/form-links       {::m.ln.peers/peers-id u.ln.peers/LNPeerForm}
   ro/source-attribute ::m.ln.peers/index
   ro/title            "Remote Node Peers"
   ro/row-pk           m.ln.peers/id
   ro/run-on-mount?    true
   ro/route            "node-peers"}
  (log/info :RemoteNodePeersReport/creating {:props props})
  (report/render-layout this))

(def ui-remote-node-peers-report (comp/factory RemoteNodePeersReport))

(defsc RemoteNodePeersSubPage
  [_this {:keys   [report] :as props
          node-id ::m.ln.remote-nodes/id}]
  {:query         [::m.ln.remote-nodes/id
                   {:report (comp/get-query RemoteNodePeersReport)}]
   :componentDidMount
   (fn [this]
     (let [props (comp/props this)]
       (log/info :RemoteNodePeersSubPage/did-mount {:props props :this this})
       (report/start-report! this RemoteNodePeersReport)))
   :initial-state {::m.ln.remote-nodes/id nil
                   :report                {}}
   :ident         (fn [] [:component/id ::NodePeersSubPage])}
  (log/info :NodePeersSubPage/creating {:props props})
  (let [peer-data (assoc-in report [:ui/parameters ::m.ln.remote-nodes/id] node-id)]
    (dom/div :.ui.segment
      (dom/h2 "Peers")
      (if node-id
        (ui-remote-node-peers-report peer-data)
        (dom/div {} "Node ID not set")))))

(def ui-remote-node-peers-sub-page (comp/factory RemoteNodePeersSubPage))
