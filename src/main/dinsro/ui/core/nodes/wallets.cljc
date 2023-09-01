(ns dinsro.ui.core.nodes.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.wallets :as mu.c.wallets]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/core/wallets.cljc]]
;; [[../../../model/core/wallets.cljc]]
;; [[../../../../../test/dinsro/ui/core/node_wallets_test.cljs]]

(def index-page-id :core-nodes-show-wallets)
(def model-key ::m.c.wallets/id)
(def parent-model-key ::m.c.nodes/id)
(def parent-router-id :core-nodes-show)
(def required-role :user)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.wallets/delete!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.wallets/node #(u.links/ui-core-node-link %2)
                         ::m.c.wallets/name #(u.links/ui-wallet-link %3)
                         ::m.c.wallets/user #(u.links/ui-user-link %2)}
   ro/columns           [m.c.wallets/name
                         m.c.wallets/derivation
                         m.c.wallets/key
                         m.c.wallets/user
                         m.c.wallets/network]
   ro/control-layout    {:inputs         [[::m.c.nodes/id]]
                         :action-buttons [::new ::refresh]}
   ro/controls          {::new          u.c.wallets/new-action-button
                         ::m.c.nodes/id {:type :uuid :label "Nodes"}
                         ::refresh      u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "wallets"
   ro/row-actions       [delete-action]
   ro/row-pk            m.c.wallets/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.wallets/index
   ro/title             "Wallets"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.c.nodes/id  nil
                       ::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.c.nodes/id
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/label         "Wallets"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
