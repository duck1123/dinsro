(ns dinsro.ui.admin.core.wallets.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.wallet-addresses :as j.c.wallet-addresses]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.wallet-addresses :as mu.c.wallet-addresses]
   [dinsro.mutations.core.wallets :as mu.c.wallets]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/addresses.cljc]]
;; [[../../../../joins/core/wallet_addresses.cljc]]
;; [[../../../../model/core/addresses.cljc]]

(def index-page-key :admin-core-wallets-show-addresses)
(def model-key ::m.c.wallet-addresses/id)
(def parent-model-key ::m.c.wallets/id)

(def generate-action
  (u.buttons/row-action-button "Generate" model-key mu.c.wallet-addresses/generate!))

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes    [m.c.wallet-addresses/address
                     m.c.wallet-addresses/wallet]
   fo/field-styles  {::m.c.wallet-addresses/wallet :pick-one}
   fo/field-options {::m.c.wallet-addresses/wallet u.pickers/admin-wallet-picker}
   fo/id            m.c.wallet-addresses/id
   fo/route-prefix  "new-wallet-address"
   fo/title         "New Wallet Address"})

(def generate-button
  {:type   :button
   :local? true
   :label  "Generate"
   :action (fn [this props]
             (let [{::m.c.wallet-addresses/keys [id]} props]
               (log/info :generate-button/clicked {:props props})
               (comp/transact! this [(mu.c.wallet-addresses/generate! {model-key id})])))})

(form/defsc-form WalletAddressForm
  [_this _props]
  {fo/action-buttons [::generate]
   fo/attributes     [m.c.wallet-addresses/address
                      m.c.wallet-addresses/wallet]
   fo/controls       {::generate generate-button}
   fo/field-styles   {::m.c.wallet-addresses/wallet :pick-one}
   fo/field-options  {::m.c.wallet-addresses/wallet u.pickers/wallet-picker}
   fo/id             m.c.wallet-addresses/id
   fo/route-prefix   "wallet-address"
   fo/title          "Wallet Address"})

(def new-action-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.wallet-addresses/address #(u.links/ui-admin-address-link %2)
                         ::m.c.wallet-addresses/wallet #(u.links/ui-wallet-link %2)}
   ro/columns           [m.c.wallet-addresses/path-index
                         m.c.wallet-addresses/address]
   ro/control-layout    {:inputs         [[parent-model-key]]
                         :action-buttons [::new ::calculate ::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::new            new-action-button
                         ::refresh        u.links/refresh-control
                         ::calculate      (u.buttons/report-action-button "Calculate" model-key mu.c.wallets/calculate-addresses!)}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "wallets-addresses"
   ro/row-actions       [generate-action]
   ro/row-pk            m.c.wallet-addresses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.wallet-addresses/admin-index
   ro/title             "Addresses"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.c.wallets/keys [id]
          :ui/keys           [report]
          :as                props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.c.wallets/id nil
                       ::m.navlinks/id  index-page-key
                       :ui/report       {}}
   :query             [::m.c.wallets/id
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (u.debug/load-error props "admin wallet addresses page")))

(def ui-sub-page (comp/factory SubPage))
