(ns dinsro.ui.core-peers
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core-peers :as m.core-peers]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]))

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
