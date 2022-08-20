(ns dinsro.ui.ln.node-peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.ui.ln.peers :as u.ln.peers]
   [lambdaisland.glogi :as log]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.mutations.ln.peers :as mu.ln.peers]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.form :as form]
   [dinsro.ui.links :as u.links]))

(def delete-button
  {:type   :button
   :local? true
   :label  "Delete"
   :action (fn [this _]
             (let [{::m.ln.peers/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.ln.peers/delete! {::m.ln.peers/id id})])))})

(def delete-action-button
  "Delete button for reports"
  {:type   :button
   :local? true
   :label  "Delete"
   :action (fn [this {::m.ln.peers/keys [id]}]
             (log/info :delete-action/clicked {:id id})
             (comp/transact! this [(mu.ln.peers/delete! {::m.ln.peers/id id})]))})

(report/defsc-report Report
  [this props]
  {ro/columns        [m.ln.peers/address
                      m.ln.peers/remote-node
                      m.ln.peers/sat-recv
                      m.ln.peers/sat-sent
                      m.ln.peers/inbound]
   ro/control-layout {:action-buttons [::new ::refresh]
                      :inputs         [[::m.ln.nodes/id]]}
   ro/controls       {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                      ::refresh       u.links/refresh-control
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

   ro/field-formatters {::m.ln.peers/block       #(u.links/ui-block-link %2)
                        ::m.ln.peers/node        #(u.links/ui-core-node-link %2)
                        ::m.ln.peers/remote-node #(u.links/ui-remote-node-link %2)}
   ro/form-links       {::m.ln.peers/peers-id u.ln.peers/LNPeerForm}
   ro/row-actions      [delete-action-button]
   ro/source-attribute ::m.ln.peers/index
   ro/title            "Node Peers"
   ro/row-pk           m.ln.peers/id
   ro/run-on-mount?    true
   ro/route            "node-peers"}
  (log/info :Report/creating {:props props})
  (report/render-layout this))

(def ui-node-peers-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props
          node-id  ::m.ln.nodes/id}]
  {:query             [::m.ln.nodes/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.ln.nodes/id nil
                       :ui/report      {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/info :SubPage/creating {:props props})
  (dom/div :.ui.segment
    (if node-id
      (ui-node-peers-report report)
      (dom/div {} "Node ID not set"))))

(def ui-node-peers-sub-page (comp/factory SubPage))
