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
  {ro/columns        [m.c.wallets/name
                      m.c.wallets/derivation
                      m.c.wallets/key]
   ro/control-layout {:action-buttons [::new]}
   ro/controls       {::new u.c.wallets/new-action-button
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
   ro/title            "Node Wallets"}
  (log/info :NodeWalletsReport/creating {:props props})
  (report/render-layout this))

(def ui-node-wallets-report (comp/factory NodeWalletsReport))

(defsc NodeWalletsSubPage
  [_this {:keys   [report] :as props
          node-id ::m.c.nodes/id}]
  {:query         [::m.c.nodes/id
                   {:report (comp/get-query NodeWalletsReport)}]
   :componentDidMount
   (fn [this]
     (let [props (comp/props this)]
       (log/info :NodeWalletsSubPage/did-mount {:props props :this this})
       (report/start-report! this NodeWalletsReport)))
   :initial-state {::m.c.nodes/id nil
                   :report        {}}
   :ident         (fn [] [:component/id ::NodeWalletsSubPage])}
  (log/info :NodeWalletsSubPage/creating {:props props})
  (let [wallet-data (assoc-in report [:ui/parameters ::m.c.nodes/id] node-id)]
    (dom/div :.ui.segment
      (if node-id
        (ui-node-wallets-report wallet-data)
        (dom/p {} "Node ID not set")))))

(def ui-node-wallets-sub-page (comp/factory NodeWalletsSubPage))
