(ns dinsro.ui.admin.core.nodes.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   ;; [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.wallets :as mu.c.wallets]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/wallets.cljc]]
;; [[../../../../model/core/wallets.cljc]]

(def index-page-key :admin-core-nodes-show-wallets)
(def model-key ::m.c.wallets/id)
(def parent-model-key ::m.c.nodes/id)
(def router-key :dinsro.ui.admin.core.nodes/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.wallets/delete!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.wallets/node #(u.links/ui-admin-core-node-link %2)
                         ::m.c.wallets/name #(u.links/ui-admin-wallet-link %3)
                         ::m.c.wallets/user #(u.links/ui-admin-user-link %2)}
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
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [_props]
                        {parent-model-key nil
                         ::m.navlinks/id  index-page-key
                         :ui/report       {}})
   :query             (fn [_props]
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (get props parent-model-key)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin nodes show wallets report"))
    (u.debug/load-error props "admin nodes show wallets")))
