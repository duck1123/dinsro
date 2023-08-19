(ns dinsro.ui.admin.ln.nodes.wallet-addresses
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
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-key :admin-ln-nodes-show-wallet-addresses)
(def model-key ::m.c.wallet-addresses/id)
(def parent-model-key ::m.ln.nodes/id)
(def parent-show-key :admin-ln-nodes-show)
(def parent-router :admin-ln-nodes)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.wallet-addresses/delete!))

(def generate-action
  (u.buttons/row-action-button "Generate" model-key mu.c.wallet-addresses/generate!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.wallet-addresses/address #(u.links/ui-admin-address-link %2)
                         ::m.c.wallet-addresses/wallet  #(u.links/ui-admin-wallet-link %2)}
   ro/columns           [m.c.wallet-addresses/path-index
                         m.c.wallet-addresses/address
                         m.c.wallet-addresses/wallet]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [generate-action delete-action]
   ro/row-pk            m.c.wallet-addresses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.wallet-addresses/admin-index
   ro/title             "Wallet Address Report"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key props
                         ::m.navlinks/id  index-page-key})
   :query             (fn []
                        [[::dr/id parent-router]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (get props parent-model-key)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin nodes show wallet addresses report"))
    (u.debug/load-error props "admin nodes show wallet addresses")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Wallet Addresses"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-show-key
   ::m.navlinks/router        parent-router
   ::m.navlinks/required-role :admin})
