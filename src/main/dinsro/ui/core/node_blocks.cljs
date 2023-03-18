(ns dinsro.ui.core.node-blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(defn fetch-action
  [report-instance {::m.c.blocks/keys [id]}]
  (comp/transact! report-instance [(mu.c.blocks/fetch! {::m.c.blocks/id id})]))

(def fetch-action-button
  {:label  "Fetch"
   :action fetch-action
   :style  :fetch-button})

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
                        m.c.blocks/fetched?]
   ro/controls         {::refresh      u.links/refresh-control
                        ::generate generate-button
                        ::m.c.nodes/id {:type :uuid :label "Nodes"}}
   ro/control-layout   {:action-buttons [::generate ::refresh]}
   ro/field-formatters {::m.c.blocks/hash (u.links/report-link ::m.c.blocks/hash u.links/ui-block-link)}
   ro/source-attribute ::m.c.blocks/index
   ro/title            "Node Blocks"
   ro/row-actions      [fetch-action-button
                        u.c.blocks/delete-action-button]
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true
   ro/route            "blocks"})

(def ui-report (comp/factory Report))

(def ident-key ::m.c.nodes/id)
(def router-key :dinsro.ui.core.nodes/Router)

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["blocks"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (if (get-in props [[::dr/id router-key] ident-key])
    (ui-report report)
    (dom/div  :.ui.segment
      (dom/h3 {} "Node ID not set")
      (u.links/ui-props-logger props))))
