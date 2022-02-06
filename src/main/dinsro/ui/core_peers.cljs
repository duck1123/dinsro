(ns dinsro.ui.core-peers
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.core-peers :as m.core-peers]
   [dinsro.mutations.core-peers :as mu.core-peers]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]))

(declare CorePeerForm)

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [{::m.core-peers/keys [id]} (comp/props this)]
               (comp/transact!
                this
                [(mu.core-peers/create! {::m.core-peers/id id})])
               (form/view! this CorePeerForm id)))})

(form/defsc-form NewCorePeerForm
  [_this _props]
  {fo/id           m.core-peers/id
   fo/action-buttons [::submit]
   fo/attributes   [m.core-peers/addr
                    m.core-peers/node]
   fo/controls {::submit submit-button}
   fo/field-options {::m.core-peers/node
                     {::picker-options/query-key       ::m.core-nodes/index
                      ::picker-options/query-component u.links/CoreNodeLinkForm
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.core-nodes/keys [id name]}]
                           {:text  (str name)
                            :value [::m.core-nodes/id id]})
                         (sort-by ::m.core-nodes/name options)))}}
   fo/field-styles {::m.core-peers/node :pick-one}
   fo/route-prefix "new-core-peer"
   fo/title        "New Core Peer"})

(form/defsc-form CorePeerForm
  [_this _props]
  {fo/id           m.core-peers/id
   fo/attributes   []
   fo/route-prefix "core-peer"
   fo/title        "Core Peer"})

(report/defsc-report CorePeersReport
  [_this _props]
  {ro/columns          [m.core-peers/addr
                        m.core-peers/address-bind
                        m.core-peers/subver
                        m.core-peers/peer-id
                        m.core-peers/node]
   ro/field-formatters {::m.core-peers/block (fn [_this props] (u.links/ui-block-link props))
                        ::m.core-peers/node  (fn [_this props] (u.links/ui-core-node-link props))}
   ro/form-links       {::m.core-peers/peers-id CorePeerForm}
   ro/source-attribute ::m.core-peers/index
   ro/title            "Core Peers"
   ro/row-pk           m.core-peers/id
   ro/run-on-mount?    true
   ro/route            "core-peers"})
