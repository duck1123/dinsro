(ns dinsro.ui.core.node-wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
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
                        m.c.wallets/derivation
                        m.c.wallets/key
                        m.c.wallets/user
                        m.c.wallets/node]
   ro/control-layout   {:inputs         [[::m.c.nodes/id]]
                        :action-buttons [::new ::refresh]}
   ro/controls         {::new u.c.wallets/new-action-button
                        ::m.c.nodes/id
                        {:type  :uuid
                         :label "Nodes"}
                        ::refresh
                        {:type   :button
                         :label  "Refresh"
                         :action (fn [this] (control/run! this))}}
   ro/field-formatters {::m.c.wallets/node #(u.links/ui-core-node-link %2)
                        ::m.c.wallets/name (u.links/report-link ::m.c.wallets/name u.links/ui-wallet-link)
                        ::m.c.wallets/user #(u.links/ui-user-link %2)}
   ro/route            "wallets"
   ro/row-actions      [u.c.wallets/delete-action-button]
   ro/row-pk           m.c.wallets/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.c.wallets/index
   ro/title            "Wallets"}
  (log/info :NodeWalletsReport/creating {:props props})
  (report/render-layout this))

(def ui-node-wallets-report (comp/factory NodeWalletsReport))

(defsc NodeWalletsSubPage
  [_this {:ui/keys [report] :as props
          node-id  ::m.c.nodes/id}]
  {:query         [::m.c.nodes/id
                   {:ui/report (comp/get-query NodeWalletsReport)}]
   :componentDidMount
   (fn [this]
     (let [{::m.c.nodes/keys [id] :as props} (comp/props this)]
       (log/info :NodeWalletsSubPage/did-mount {:props props :this this})
       (report/start-report! this NodeWalletsReport {:route-params {::m.c.nodes/id id}})))
   :initial-state {::m.c.nodes/id nil
                   :ui/report     {}}
   :ident         (fn [] [:component/id ::NodeWalletsSubPage])}
  (log/info :NodeWalletsSubPage/creating {:props props})
  (dom/div :.ui.segment
    (if node-id
      (ui-node-wallets-report report)
      (dom/p {} "Node ID not set"))))

(def ui-node-wallets-sub-page (comp/factory NodeWalletsSubPage))
