(ns dinsro.ui.core.node-wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [lambdaisland.glogi :as log]))

(defsc NodeWalletsSubPage
  [_this {:keys   [report] :as props
          node-id ::m.c.nodes/id}]
  {:query         [::m.c.nodes/id
                   {:report (comp/get-query u.c.wallets/WalletsReport)}]
   :pre-merge
   (fn [{:keys [data-tree state-map]}]
     (log/info :NodeWalletsSubPage/pre-merge {:data-tree data-tree})
     (let [initial             (comp/get-initial-state u.c.wallets/WalletsReport)
           report-data         (get-in state-map (comp/get-ident u.c.wallets/WalletsReport {}))
           updated-report-data (merge initial report-data)
           updated-data        (-> data-tree
                                   (assoc :wallets updated-report-data))]
       (log/info :NodeWalletsSubPage/merged {:updated-data updated-data :data-tree data-tree})
       updated-data))
   :initial-state {::m.c.nodes/id nil
                   :report        {}}
   :ident         ::m.c.nodes/id}
  (log/info :NodeWalletsSubPage/creating {:props props})
  (let [wallet-data (assoc-in report [:ui/parameters ::m.c.nodes/id] node-id)]
    (dom/div :.ui.segment
      (u.c.wallets/ui-wallets-report wallet-data))))

(def ui-node-wallets-sub-page (comp/factory NodeWalletsSubPage))
