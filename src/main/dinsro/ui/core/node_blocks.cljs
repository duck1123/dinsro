(ns dinsro.ui.core.node-blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [lambdaisland.glogi :as log]))

(defsc NodeBlocksSubPage
  [_this {:keys   [report] :as props
          node-id ::m.c.nodes/id}]
  {:query         [::m.c.nodes/id
                   {:report (comp/get-query u.c.blocks/CoreBlockReport)}]
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
  (let [block-data (assoc-in report [:ui/parameters ::m.c.nodes/id] node-id)]
    (dom/div :.ui.segment
      (if node-id
        (u.c.blocks/ui-blocks-report block-data)
        (dom/p {} "Node ID not set")))))

(def ui-node-blocks-sub-page (comp/factory NodeBlocksSubPage))
