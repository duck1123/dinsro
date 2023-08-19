(ns dinsro.ui.ln.nodes.wallet-addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.wallet-addresses :as j.c.wallet-addresses]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.wallet-addresses :as mu.c.wallet-addresses]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/core/wallet_addresses.cljc]]
;; [[../../../model/core/wallet_addresses.cljc]]

(def index-page-key :ln-nodes-show-wallet-addresses)
(def model-key ::m.c.wallet-addresses/id)
(def parent-model-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(def generate-action
  (u.buttons/subrow-action-button "Generate" model-key parent-model-key mu.c.wallet-addresses/generate!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.wallet-addresses/wallet #(u.links/ui-wallet-link %2)
                         ::m.c.wallet-addresses/address #(u.links/ui-admin-address-link %2)}
   ro/columns           [m.c.wallet-addresses/address
                         m.c.wallet-addresses/wallet
                         m.c.wallet-addresses/path-index]
   ro/control-layout    {:action-buttons [::refresh]
                         :inputs         [[::m.ln.nodes/id]]}
   ro/controls          {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [generate-action]
   ro/row-pk            m.c.wallet-addresses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.wallet-addresses/index
   ro/title             "Wallet Addresses"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [_]
                        {::m.navlinks/id index-page-key
                         :ui/report      {}})
   :query             (fn [_]
                        [[::dr/id router-key]
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["wallet-addresses"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (ui-report report))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Wallet Addresses"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :ln-nodes-show
   ::m.navlinks/router        :ln-nodes
   ::m.navlinks/required-role :user})
