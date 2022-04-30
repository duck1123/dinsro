(ns dinsro.ui.ln.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.mutations.ln.peers :as mu.ln.peers]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(defsc LnPeerRow
  [_this {::m.ln.peers/keys [address pubkey inbound sat-sent]}]
  {}
  (dom/tr {}
    (dom/td address)
    (dom/td pubkey)
    (dom/td (str inbound))
    (dom/td sat-sent)))

(def ui-ln-peer-row (comp/factory LnPeerRow {:keyfn ::m.ln.peers/id}))

(defn ref-ln-peer-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "Address")
         (dom/th {} "pubkey")
         (dom/th {} "inbound")
         (dom/th {} "sats sent")))
     (dom/tbody {}
       (for [peer value]
         (ui-ln-peer-row peer))))))

(def render-ref-ln-peer-row (render-field-factory ref-ln-peer-row))

(form/defsc-form PeerSubform
  [_this _props]
  {fo/id           m.ln.peers/id
   fo/route-prefix "nodes-peers"
   fo/title        "Peers"
   fo/attributes   [m.ln.peers/address
                    m.ln.peers/pubkey
                    m.ln.peers/inbound
                    m.ln.peers/sat-sent]})

(form/defsc-form NodeLink [_this _props]
  {fo/id           m.ln.nodes/id
   fo/route-prefix "peers-node"
   fo/attributes   [m.ln.nodes/name]})

(form/defsc-form LNPeerForm [_this _props]
  {fo/id           m.ln.peers/id
   fo/attributes   [m.ln.peers/address
                    m.ln.peers/pubkey
                    m.ln.peers/inbound
                    m.ln.peers/sat-sent
                    m.ln.peers/node]
   fo/subforms     {::m.ln.peers/node {::form/ui NodeLink}
                    ::m.ln.nodes/id   {::form/ui NodeLink}}
   fo/field-styles {::m.ln.peers/node :link
                    ::m.ln.peers/id   :link}
   fo/route-prefix "peer"
   fo/title        "Lightning Peer"})

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [{::m.ln.peers/keys [addr id]
                    node             ::m.ln.peers/node} (comp/props this)
                   {node-id ::m.ln.nodes/id}            node
                   props                               {::m.ln.peers/id   id
                                                        ::m.ln.peers/addr addr
                                                        ::m.ln.peers/node node-id}]
               (log/info :submit-action/clicked props)
               (comp/transact! this [(mu.ln.peers/create! props)])
               (form/view! this LNPeerForm id)))})

(form/defsc-form NewPeerForm [_this _props]
  {fo/id           m.ln.peers/id
   fo/action-buttons [::submit]
   fo/attributes   [m.ln.peers/address
                    m.ln.peers/pubkey
                    m.ln.peers/node]
   fo/controls {::submit submit-button}
   fo/field-options  {::m.ln.peers/node
                      {::picker-options/query-key       ::m.ln.nodes/index
                       ::picker-options/query-component u.links/NodeLinkForm
                       ::picker-options/options-xform
                       (fn [_ options]
                         (mapv
                          (fn [{::m.ln.nodes/keys [id name]}]
                            {:text  (str name)
                             :value [::m.ln.nodes/id id]})
                          (sort-by ::m.ln.nodes/name options)))}}
   fo/subforms     {::m.ln.peers/node {::form/ui NodeLink}
                    ::m.ln.nodes/id   {::form/ui NodeLink}}
   fo/field-styles {::m.ln.peers/node :pick-one
                    ::m.ln.peers/id   :link}
   fo/route-prefix "new-peer"
   fo/title        "New Peer"})

(def override-report true)

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewPeerForm))})

(report/defsc-report LNPeersReport
  [this _props]
  {ro/columns          [m.ln.peers/pubkey
                        m.ln.peers/inbound
                        m.ln.peers/node]
   ro/controls {::new new-button}
   ro/field-formatters {::m.ln.peers/node (fn [_this props] (u.links/ui-node-link props))}
   ro/form-links       {::m.ln.peers/pubkey LNPeerForm}
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
