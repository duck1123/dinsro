(ns dinsro.ui.core.node-wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(report/defsc-report NodeWalletsReport
  [this props]
  {ro/columns          [m.c.wallets/name
                        m.c.wallets/node
                        m.c.wallets/user]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new u.c.wallets/new-action-button
                        ::m.c.nodes/id
                        {:type  :uuid
                         :label "Nodes"}}
   ro/field-formatters {::m.c.wallets/node #(u.links/ui-core-node-link %2)
                        ::m.c.wallets/user #(u.links/ui-user-link %2)}
   ro/form-links       {::m.c.wallets/name u.c.wallets/WalletForm}
   ro/route            "wallets"
   ro/row-actions      [u.c.wallets/delete-action-button]
   ro/row-pk           m.c.wallets/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.c.wallets/index
   ro/title            "Node Wallet Report"}
  (log/info :NodeWalletsReport/creating {:props props})
  (report/render-layout this))

(def ui-node-wallets-report (comp/factory NodeWalletsReport))

(defsc NodeWalletsSubPage
  [_this {:keys   [report] :as props
          node-id ::m.c.nodes/id}]
  {:query         [::m.c.nodes/id
                   {:report (comp/get-query NodeWalletsReport)}]
   :pre-merge
   (fn [{:keys [data-tree state-map]}]
     (log/info :NodeWalletsSubPage/pre-merge {:data-tree data-tree})
     (let [initial             (comp/get-initial-state NodeWalletsReport)
           report-data         (get-in state-map (comp/get-ident NodeWalletsReport {}))
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
      (ui-node-wallets-report wallet-data))))

(def ui-node-wallets-sub-page (comp/factory NodeWalletsSubPage))
