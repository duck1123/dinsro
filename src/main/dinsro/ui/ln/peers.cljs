(ns dinsro.ui.ln.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.mutations.ln.peers :as mu.ln.peers]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [{::m.ln.peers/keys [address id]
                    node             ::m.ln.peers/node} (comp/props this)
                   {node-id ::m.ln.nodes/id}            node
                   props                               {::m.ln.peers/id   id
                                                        ::m.ln.peers/address address
                                                        ::m.ln.peers/node node-id}]
               (log/info :submit-action/clicked props)
               (comp/transact! this [(mu.ln.peers/create! props)])))})

(form/defsc-form NewPeerForm [_this _props]
  {fo/id             m.ln.peers/id
   fo/action-buttons [::submit]
   fo/attributes     [m.ln.peers/node
                      m.ln.peers/remote-node]
   fo/controls       {::submit submit-button}
   fo/field-options  {::m.ln.peers/node
                      {::picker-options/query-key       ::m.ln.nodes/index
                       ::picker-options/query-component u.links/NodeLinkForm
                       ::picker-options/options-xform
                       (fn [_ options]
                         (mapv
                          (fn [{::m.ln.nodes/keys [id name]}]
                            {:text  (str name)
                             :value [::m.ln.nodes/id id]})
                          (sort-by ::m.ln.nodes/name options)))}
                      ::m.ln.peers/remote-node
                      {::picker-options/query-key       ::m.ln.remote-nodes/index
                       ::picker-options/query-component u.links/RemoteNodeLinkForm
                       ::picker-options/options-xform
                       (fn [_ options]
                         (mapv
                          (fn [{::m.ln.remote-nodes/keys [id pubkey]}]
                            {:text  (str pubkey)
                             :value [::m.ln.remote-nodes/id id]})
                          (sort-by ::m.ln.remote-nodes/pubkey options)))}}
   fo/field-styles   {::m.ln.peers/node        :pick-one
                      ::m.ln.peers/remote-node :pick-one}
   fo/route-prefix   "new-peer"
   fo/title          "New Peer"})

(def override-report true)

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewPeerForm))})

(report/defsc-report Report
  [this _props]
  {ro/columns          [m.ln.peers/node
                        m.ln.peers/remote-node
                        m.ln.peers/inbound?]
   ro/controls {::new new-button}
   ro/field-formatters {::m.ln.peers/node #(u.links/ui-node-link %2)
                        ::m.ln.peers/pubkey #(u.links/ui-ln-peer-link %3)
                        ::m.ln.peers/remote-node #(u.links/ui-remote-node-link %2)}
   ro/route            "peers"
   ro/row-actions      []
   ro/row-pk           m.ln.peers/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.peers/index
   ro/title            "Lightning Peers"}
  (if override-report
    (report/render-layout this)
    (dom/div :.ui
      (report/render-layout this))))
