(ns dinsro.ui.core.node-peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.ui.core.peers :as u.c.peers]
   [lambdaisland.glogi :as log]))

(defsc NodePeersSubPage
  [_this {:keys   [report] :as props
          node-id ::m.c.nodes/id}]
  {:query         [::m.c.nodes/id
                   {:report (comp/get-query u.c.peers/CorePeersReport)}]
   :pre-merge
   (fn [{:keys [data-tree state-map]}]
     (log/info :NodePeersSubPage/pre-merge {:data-tree data-tree})
     (let [initial             (comp/get-initial-state u.c.peers/CorePeersReport)
           report-data         (get-in state-map (comp/get-ident u.c.peers/CorePeersReport {}))
           updated-report-data (merge initial report-data)
           updated-data        (-> data-tree
                                   (assoc :report updated-report-data))]
       (log/info :NodePeersSubPage/merged {:updated-data updated-data :data-tree data-tree})
       updated-data))
   :initial-state {::m.c.nodes/id nil
                   :report        {}}
   :ident         ::m.c.nodes/id}
  (log/info :NodePeersSubPage/creating {:props props})
  (let [peer-data (assoc-in report [:ui/parameters ::m.c.nodes/id] node-id)]
    (dom/div :.ui.segment
      (if node-id
        (u.c.peers/ui-peers-report peer-data)
        (dom/div {} "Node ID not set")))))

(def ui-node-peers-sub-page (comp/factory NodePeersSubPage))
