(ns dinsro.ui.ln.node-remote-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def make-peer-action-button
  {:type   :button
   :local? true
   :label  "Make Peer"
   :action (fn [this props]
             (let [node-id        (u.links/get-control-value this ::m.ln.nodes/id)
                   remote-node-id (::m.ln.remote-nodes/id props)]
               (log/info :make-peer-action-button/clicked
                         {:node-id        node-id
                          :remote-node-id remote-node-id})
               (comp/transact! this [(mu.ln.nodes/make-peer!
                                      {::m.ln.nodes/id        node-id
                                       ::m.ln.remote-nodes/id remote-node-id})])))})

(report/defsc-report Report
  [this props]
  {ro/columns          [m.ln.remote-nodes/pubkey
                        m.ln.remote-nodes/host
                        m.ln.remote-nodes/alias
                        m.ln.remote-nodes/color
                        m.ln.remote-nodes/node]
   ro/control-layout   {:action-buttons [::refresh]
                        :inputs         [[::m.ln.nodes/id]]}
   ro/controls         {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.ln.remote-nodes/block #(u.links/ui-block-link %2)
                        ::m.ln.remote-nodes/node  #(u.links/ui-core-node-link %2)}
   ro/source-attribute ::m.ln.remote-nodes/index
   ro/title            "Node Remote-Nodes"
   ro/row-actions      [make-peer-action-button]
   ro/row-pk           m.ln.remote-nodes/id
   ro/run-on-mount?    true}
  (log/info :Report/starting {:props props})
  (report/render-layout this))

(def ui-report (comp/factory Report))

(def ident-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["remote-nodes"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (if (get-in props [[::dr/id router-key] ident-key])
    (ui-report report)
    (dom/div  :.ui.segment
      (dom/h3 {} "Node ID not set")
      (u.links/log-props props))))

(def ui-sub-page (comp/factory SubPage))
