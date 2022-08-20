(ns dinsro.ui.core.node-peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.core.peers :as u.c.peers]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(report/defsc-report Report
  [this props]
  {ro/columns        [m.c.peers/peer-id
                      m.c.peers/addr
                      m.c.peers/subver
                      m.c.peers/connection-type]
   ro/control-layout {:action-buttons [::new ::fetch ::refresh]
                      :inputs         [[::m.c.nodes/id]]}
   ro/controls       {::m.c.nodes/id {:type :uuid :label "Nodes"}
                      ::refresh      u.links/refresh-control
                      ::fetch        {:type   :button
                                      :label  "Fetch"
                                      :action (fn [this]
                                                (let [props                 (comp/props this)
                                                      {:ui/keys [controls]} props
                                                      id-control            (some
                                                                             (fn [c]
                                                                               (let [{::control/keys [id]} c]
                                                                                 (when (= id ::m.c.nodes/id)
                                                                                   c)))
                                                                             controls)
                                                      node-id               (::control/value id-control)]
                                                  (comp/transact! this [(mu.c.nodes/fetch! {::m.c.nodes/id node-id})])))}
                      ::new          {:type   :button
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
                                                  (form/create! this u.c.peers/NewCorePeerForm
                                                                {:initial-state {::m.c.peers/addr "foo"}})))}}
   ro/field-formatters {::m.c.peers/block #(u.links/ui-block-link %2)
                        ::m.c.peers/node  #(u.links/ui-core-node-link %2)}
   ro/form-links       {::m.c.peers/peers-id u.c.peers/CorePeerForm}
   ro/row-actions      [u.c.peers/delete-action-button]
   ro/source-attribute ::m.c.peers/index
   ro/title            "Node Peers"
   ro/row-pk           m.c.peers/id
   ro/run-on-mount?    true
   ro/route            "node-peers"}
  (log/info :Report/creating {:props props})
  (report/render-layout this))

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props
          node-id  ::m.c.nodes/id}]
  {:query             [::m.c.nodes/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.nodes/id nil
                       :ui/report     {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/info :SubPage/creating {:props props})
  (dom/div :.ui.segment
    (if node-id
      (ui-report report)
      (dom/div {} "Node ID not set"))))

(def ui-sub-page (comp/factory SubPage))
