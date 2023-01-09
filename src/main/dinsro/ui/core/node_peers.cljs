(ns dinsro.ui.core.node-peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
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

(def fetch-button
  {:type   :button
   :label  "Fetch"
   :action (u.links/report-action ::m.c.nodes/id mu.c.nodes/fetch!)})

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
                      ::fetch        fetch-button
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
   ro/row-actions      [u.c.peers/delete-action-button]
   ro/source-attribute ::m.c.peers/index
   ro/title            "Node Peers"
   ro/row-pk           m.c.peers/id
   ro/run-on-mount?    true
   ro/route            "node-peers"}
  (log/info :Report/creating {:props props})
  (report/render-layout this))

(def ui-report (comp/factory Report))

(def ident-key ::m.c.nodes/id)
(def router-key :dinsro.ui.core.nodes/Router)

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["peers"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (if (get-in props [[::dr/id router-key] ident-key])
    (ui-report report)
    (dom/div  :.ui.segment
      (dom/h3 {} "Node ID not set")
      (u.links/ui-props-logger props))))

(def ui-sub-page (comp/factory SubPage))
