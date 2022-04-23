(ns dinsro.ui.core.node-blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(report/defsc-report NodeBlocksReport
  [_this _props]
  {ro/columns
   [m.c.blocks/hash
    m.c.blocks/height
    m.c.blocks/fetched?
    m.c.blocks/node]

   ro/controls
   {::search u.c.blocks/search-control

    ::refresh
    {:type   :button
     :label  "Refresh"
     :action (fn [this] (control/run! this))}

    ::m.c.nodes/id
    {:type  :uuid
     :label "Nodes"}}

   ro/control-layout   {:inputs         [[::block-id ::m.c.blocks/node ::search]]
                        :action-buttons [::refresh]}
   ro/field-formatters {::m.c.blocks/node (fn [_ props] (u.links/ui-core-node-link props))}
   ro/form-links       {::m.c.blocks/hash u.c.blocks/CoreBlockForm}
   ro/source-attribute ::m.c.blocks/index
   ro/title            "Core Blocks"
   ro/row-actions      [u.c.blocks/delete-action-button]
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true
   ro/route            "blocks"})

(def ui-blocks-report (comp/factory NodeBlocksReport))

(defsc NodeBlocksSubPage
  [_this {:keys   [report] :as props
          node-id ::m.c.nodes/id}]
  {:query         [::m.c.nodes/id
                   {:report (comp/get-query u.c.blocks/CoreBlockReport)}]
   :componentDidMount
   (fn [this]
     (let [props (comp/props this)]
       (log/info :NodePeersSubPage/did-mount {:props props :this this})
       (report/start-report! this NodeBlocksReport)))
   :pre-merge
   (fn [{:keys [data-tree state-map]}]
     (log/info :NodeBlocksSubPage/pre-merge {:data-tree data-tree})
     (let [initial             (comp/get-initial-state u.c.blocks/CoreBlockReport)
           report-data         (get-in state-map (comp/get-ident u.c.blocks/CoreBlockReport {}))
           updated-report-data (merge initial report-data)
           updated-data        (-> data-tree
                                   (assoc :blocks updated-report-data))]
       (log/info :NodeBlocksSubPage/merged {:updated-data updated-data :data-tree data-tree})
       updated-data))
   :initial-state {::m.c.nodes/id nil
                   :report        {}}
   :ident         (fn [] [:component/id ::NodeBlocksSubPage])}
  (log/info :NodeBlocksSubPage/creating {:props props})
  (let [block-data (assoc-in report [:ui/parameters ::m.c.blocks/node] {::m.c.nodes/id node-id})]
    (dom/div :.ui.segment
      (if node-id
        (do
          (log/info :NodeBlocksSubPage/report-rendering {:block-data block-data})
          (u.c.blocks/ui-blocks-report block-data))
        (dom/p {} "Node ID not set")))))

(def ui-node-blocks-sub-page (comp/factory NodeBlocksSubPage))
