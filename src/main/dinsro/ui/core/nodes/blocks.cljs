(ns dinsro.ui.core.nodes.blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.blocks :as j.c.blocks]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def ident-key ::m.c.nodes/id)
(def router-key :dinsro.ui.core.nodes/Router)

(def generate-button
  {:label "Generate"
   :local? true
   :type :button
   :action (fn [this _]
             (let [props (comp/props this)
                   parameters (:ui/parameters props)
                   node-id (::m.c.nodes/id parameters)]

               (log/info :generate-button/clicked {:props props :node-id node-id})
               (comp/transact! this [(mu.c.nodes/generate! {::m.c.nodes/id node-id})])))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.blocks/hash
                        m.c.blocks/height
                        m.c.blocks/fetched?
                        m.c.blocks/network]
   ro/control-layout   {:action-buttons [::generate ::refresh]}
   ro/controls         {::refresh      u.links/refresh-control
                        ::generate     generate-button
                        ::m.c.nodes/id {:type :uuid :label "Nodes"}}
   ro/field-formatters {::m.c.blocks/hash    #(u.links/ui-block-link %3)
                        ::m.c.blocks/network #(u.links/ui-network-link %2)}
   ro/route            "blocks"
   ro/row-actions      [(u.links/row-action-button "Fetch" ::m.c.blocks/id mu.c.blocks/fetch!)
                        (u.links/row-action-button "Delete" ::m.c.blocks/id mu.c.blocks/delete!)]
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.blocks/index
   ro/title            "Node Blocks"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["blocks"]}
  ((comp/factory Report) report))