(ns dinsro.ui.core.node-wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(report/defsc-report Report
  [this props]
  {ro/columns          [m.c.wallets/name
                        m.c.wallets/derivation
                        m.c.wallets/key
                        m.c.wallets/user
                        m.c.wallets/network]
   ro/control-layout   {:inputs         [[::m.c.nodes/id]]
                        :action-buttons [::new ::refresh]}
   ro/controls         {::new          u.c.wallets/new-action-button
                        ::m.c.nodes/id {:type :uuid :label "Nodes"}
                        ::refresh      u.links/refresh-control}
   ro/field-formatters {::m.c.wallets/node #(u.links/ui-core-node-link %2)
                        ::m.c.wallets/name #(u.links/ui-wallet-link %3)
                        ::m.c.wallets/user #(u.links/ui-user-link %2)}
   ro/route            "wallets"
   ro/row-pk           m.c.wallets/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.wallets/index
   ro/title            "Wallets"}
  (log/info :Report/creating {:props props})
  (report/render-layout this))

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props
          node-id  ::m.c.nodes/id}]
  {:query             [::m.c.nodes/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.nodes/id nil
                       :ui/report     {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/info :SubPage/creating {:props props})
  (dom/div :.ui.segment
    (if node-id
      (ui-report report)
      (dom/p {} "Node ID not set"))))
