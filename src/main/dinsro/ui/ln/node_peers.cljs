(ns dinsro.ui.ln.node-peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.ln.peers :as j.ln.peers]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.mutations.ln.peers :as mu.ln.peers]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.peers :as u.ln.peers]
   [lambdaisland.glogi :as log]))

(def ident-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(def fetch-button
  {:type   :button
   :label  "Fetch"
   :action (u.links/report-action ::m.ln.nodes/id mu.ln.nodes/fetch-peers!)})

(defn new-button-action
  [this]
  (let [props                 (comp/props this)
        {:ui/keys [controls]} props
        id-key                ::m.ln.nodes/id
        id-control            (some
                               (fn [c]
                                 (let [{::control/keys [id]} c]
                                   (when (= id id-key) c)))
                               controls)
        node-id (::control/value id-control)]
    (log/finer :peers/creating {:props      props
                                :controls   controls
                                :id-control id-control
                                :node-id    node-id})
    (form/create! this u.ln.peers/NewPeerForm
                  {:initial-state {::m.ln.peers/address "foo"}})))

(def new-button
  {:type   :button
   :label  "New"
   :action new-button-action})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.peers/remote-node
                        m.ln.peers/sat-recv
                        m.ln.peers/sat-sent
                        m.ln.peers/inbound?]
   ro/control-layout   {:action-buttons [::fetch ::new ::refresh]
                        :inputs         [[::m.ln.nodes/id]]}
   ro/controls         {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                        ::fetch         fetch-button
                        ::new           new-button
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.ln.peers/block       #(u.links/ui-block-link %2)
                        ::m.ln.peers/node        #(u.links/ui-core-node-link %2)
                        ::m.ln.peers/remote-node #(u.links/ui-remote-node-link %2)}
   ro/row-actions      [(u.links/row-action-button "Delete" ::m.ln.peers/id mu.ln.peers/delete!)]
   ro/source-attribute ::j.ln.peers/index
   ro/title            "Node Peers"
   ro/row-pk           m.ln.peers/id
   ro/run-on-mount?    true})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["peers"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
