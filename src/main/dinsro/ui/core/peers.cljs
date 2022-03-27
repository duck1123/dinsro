(ns dinsro.ui.core.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.mutations.core.peers :as mu.c.peers]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(declare CorePeerForm)

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [{::m.c.peers/keys [addr id]
                    node                ::m.c.peers/node} (comp/props this)
                   {node-id ::m.c.nodes/id}               node
                   props                                     {::m.c.peers/id   id
                                                              ::m.c.peers/addr addr
                                                              ::m.c.peers/node node-id}]
               (log/info :submit-action/clicked props)
               (comp/transact! this [(mu.c.peers/create! props)])
               (form/view! this CorePeerForm id)))})

(form/defsc-form NewCorePeerForm
  [_this _props]
  {fo/id           m.c.peers/id
   fo/action-buttons [::submit]
   fo/attributes   [m.c.peers/addr
                    m.c.peers/node]
   fo/controls {::submit submit-button}
   fo/field-options {::m.c.peers/node
                     {::picker-options/query-key       ::m.c.nodes/index
                      ::picker-options/query-component u.links/CoreNodeLinkForm
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.c.nodes/keys [id name]}]
                           {:text  (str name)
                            :value [::m.c.nodes/id id]})
                         (sort-by ::m.c.nodes/name options)))}}
   fo/field-styles {::m.c.peers/node :pick-one}
   fo/route-prefix "new-core-peer"
   fo/title        "New Core Peer"})

(def delete-button
  {:type   :button
   :local? true
   :label  "Delete"
   :action (fn [this _]
             (let [{::m.c.peers/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.c.peers/delete! {::m.c.peers/id id})])))})

(form/defsc-form CorePeerForm
  [_this _props]
  {fo/id             m.c.peers/id
   fo/action-buttons [::delete]
   fo/attributes     [m.c.peers/addr
                      m.c.peers/node
                      m.c.peers/subver
                      m.c.peers/peer-id]
   fo/controls       {::delete delete-button}
   fo/field-styles   {::m.c.peers/node :link}
   fo/route-prefix   "core-peer"
   fo/subforms       {::m.c.peers/node {fo/ui u.links/CoreNodeLinkForm}}
   fo/title          "Core Peer"})

(def delete-action-button
  "Delete button for reports"
  {:type   :button
   :local? true
   :label  "Delete"
   :action (fn [this {::m.c.peers/keys [id]}]
             (log/info :delete-action/clicked {:id id})
             (comp/transact! this [(mu.c.peers/delete! {::m.c.peers/id id})]))})

(report/defsc-report CorePeersReport
  [_this _props]
  {ro/columns          [m.c.peers/addr
                        m.c.peers/address-bind
                        m.c.peers/subver
                        m.c.peers/peer-id
                        m.c.peers/node]
   ro/field-formatters {::m.c.peers/block (fn [_this props] (u.links/ui-block-link props))
                        ::m.c.peers/node  (fn [_this props] (u.links/ui-core-node-link props))}
   ro/form-links       {::m.c.peers/peers-id CorePeerForm}
   ro/row-actions      [delete-action-button]
   ro/source-attribute ::m.c.peers/index
   ro/title            "Core Peers"
   ro/row-pk           m.c.peers/id
   ro/run-on-mount?    true
   ro/route            "core-peers"})
