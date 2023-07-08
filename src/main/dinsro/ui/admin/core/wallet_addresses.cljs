(ns dinsro.ui.admin.core.wallet-addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   ;; [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   ;; [com.fulcrologic.rad.form :as form]
   ;; [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   ;; [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.wallet-addresses :as j.c.wallet-addresses]
   ;; [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   ;; [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.wallet-addresses :as mu.c.wallet-addresses]
   ;; [dinsro.mutations.core.wallets :as mu.c.wallets]
   ;; [dinsro.ui.admin.core.wallets.accounts :as u.a.c.w.accounts]
   ;; [dinsro.ui.admin.core.wallets.addresses :as u.a.c.w.addresses]
   ;; [dinsro.ui.admin.core.wallets.words :as u.a.c.w.words]
   [dinsro.ui.buttons :as u.buttons]
   ;; [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   ;; [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/wallet_addresses.cljc]]
;; [[../../../model/core/wallet_addresses.cljc]]
;; [[../../../mutations/core/wallet_addresses.cljc]]

(def index-page-key :admin-core-wallet-addresses)
(def model-key ::m.c.wallet-addresses/id)

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
   ro/row-actions       [generate-action
                         delete-action]
   ro/row-pk            m.c.wallet-addresses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.wallet-addresses/admin-index
   ro/title             "Wallet Address Report"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["wallet-addresses"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))
