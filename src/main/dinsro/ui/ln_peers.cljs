(ns dinsro.ui.ln-peers
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.ln-peers :as m.ln-peers]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(form/defsc-form NodeLink [_this _props]
  {fo/id         m.ln-nodes/id
   fo/attributes [m.ln-nodes/name]})

(form/defsc-form LNPeerForm [_this _props]
  {fo/id           m.ln-peers/id
   fo/attributes   [m.ln-peers/address
                    m.ln-peers/pubkey
                    m.ln-peers/inbound
                    m.ln-peers/sat-sent
                    m.ln-peers/node]
   fo/subforms     {::m.ln-peers/node {::form/ui NodeLink}
                    ::m.ln-nodes/id   {::form/ui NodeLink}}
   fo/field-styles {::m.ln-peers/node :link
                    ::m.ln-peers/id   :link}
   fo/route-prefix "ln-peer"
   fo/title        "Lightning Peer"})

(def override-report true)

(report/defsc-report LNPeersReport
  [this _props]
  {ro/columns          [m.ln-peers/id
                        m.ln-peers/pubkey
                        m.ln-peers/inbound]
   ro/links            {::m.ln-peers/id (fn [this props]
                                          (let [{::m.ln-peers/keys [id]} props]
                                            (form/view! this LNPeerForm id)))}
   ro/route            "ln-peers"
   ro/row-actions      []
   ro/row-pk           m.ln-peers/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln-peers/all-peers
   ro/title            "Lightning Peers"}
  (if override-report
    (report/render-layout this)
    (dom/div :.ui.container
      (report/render-layout this))))
