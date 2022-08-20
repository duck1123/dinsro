(ns dinsro.ui.core.node-blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
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

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.blocks/hash
                        m.c.blocks/height
                        m.c.blocks/fetched?]
   ro/controls         {::refresh      u.links/refresh-control
                        ::m.c.nodes/id {:type :uuid :label "Nodes"}}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.blocks/node #(u.links/ui-core-node-link %2)
                        ::m.c.blocks/hash (u.links/report-link ::m.c.blocks/hash u.links/ui-block-link)}
   ro/source-attribute ::m.c.blocks/index
   ro/title            "Node Blocks"
   ro/row-actions      [fetch-action-button
                        u.c.blocks/delete-action-button]
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true
   ro/route            "blocks"})

(def ui-node-blocks-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props
          node-id  ::m.c.nodes/id}]
  {:query             [::m.c.nodes/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:query-param (comp/props %)})
   :initial-state     {::m.c.nodes/id nil
                       :ui/report     {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/finer :SubPage/creating {:props props})
  (let [block-data (assoc-in report [:ui/parameters ::m.c.nodes/id] node-id)]
    (dom/div :.ui.segment
      (if node-id
        (do
          (log/finer :SubPage/report-renderin {:block-data block-data})
          (ui-node-blocks-report block-data))
        (dom/p {} "Node ID not set")))))

(def ui-node-blocks-sub-page (comp/factory SubPage))
