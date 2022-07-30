(ns dinsro.ui.core.node-blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   ;; [com.fulcrologic.rad.form :as form]
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

(report/defsc-report NodeBlocksReport
  [_this _props]
  {ro/columns
   [m.c.blocks/hash
    m.c.blocks/height
    m.c.blocks/fetched?]

   ro/controls
   {::refresh
    {:type   :button
     :label  "Refresh"
     :action (fn [this] (control/run! this))}

    ::m.c.nodes/id
    {:type  :uuid
     :label "Nodes"}}
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

(def ui-node-blocks-report (comp/factory NodeBlocksReport))

(defsc NodeBlocksSubPage
  [_this {:keys   [report] :as props
          node-id ::m.c.nodes/id}]
  {:query         [::m.c.nodes/id
                   {:report (comp/get-query NodeBlocksReport)}]
   :componentDidMount
   (fn [this]
     (let [props (comp/props this)]
       (log/info :NodePeersSubPage/did-mount {:props props :this this})
       (report/start-report! this NodeBlocksReport)))
   :pre-merge
   (fn [{:keys [data-tree state-map]}]
     (log/info :NodeBlocksSubPage/pre-merge {:data-tree data-tree})
     (let [initial             (comp/get-initial-state NodeBlocksReport)
           report-data         (get-in state-map (comp/get-ident NodeBlocksReport {}))
           updated-report-data (merge initial report-data)
           updated-data        (-> data-tree (assoc :blocks updated-report-data))]
       (log/info :NodeBlocksSubPage/merged {:updated-data updated-data :data-tree data-tree})
       updated-data))
   :initial-state {::m.c.nodes/id nil
                   :report        {}}
   :ident         (fn [] [:component/id ::NodeBlocksSubPage])}
  (log/info :NodeBlocksSubPage/creating {:props props})
  (let [block-data (assoc-in report [:ui/parameters ::m.c.nodes/id] node-id)]
    (dom/div :.ui.segment
      (if node-id
        (do
          (log/info :NodeBlocksSubPage/report-renderin {:block-data block-data})
          (ui-node-blocks-report block-data))
        (dom/p {} "Node ID not set")))))

(def ui-node-blocks-sub-page (comp/factory NodeBlocksSubPage))
