(ns dinsro.ui.core.wallet-addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.mutations.core.wallet-addresses :as mu.c.wallet-addresses]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(form/defsc-form NewWalletAddressForm
  [_this _props]
  {fo/id         m.c.wallet-addresses/id
   fo/attributes [m.c.wallet-addresses/address

                  m.c.wallet-addresses/wallet]
   fo/field-styles {::m.c.wallet-addresses/wallet :pick-one}
   fo/field-options
   {::m.c.wallet-addresses/wallet
    {::picker-options/query-key       ::m.c.wallets/index
     ::picker-options/query-component u.links/WalletLinkForm
     ::picker-options/options-xform
     (fn [_ options]
       (mapv
        (fn [{::m.c.wallets/keys [id name]}]
          {:text  (str name)
           :value [::m.c.wallets/id id]})
        (sort-by ::m.c.wallets/name options)))}}
   fo/route-prefix "new-wallet-address"
   fo/title        "New Wallet Address"})

(def generate-button
  {:type   :button
   :local? true
   :label  "Generate"
   :action (fn [this props]
             (let [{::m.c.wallet-addresses/keys [id]} props]
               (log/info :generate-button/clicked {:props props})
               (comp/transact! this [(mu.c.wallet-addresses/generate! {::m.c.wallet-addresses/id id})])))})

(form/defsc-form WalletAddressForm
  [_this _props]
  {fo/id             m.c.wallet-addresses/id
   fo/action-buttons [::generate]
   fo/attributes     [m.c.wallet-addresses/address
                      m.c.wallet-addresses/wallet]
   fo/controls       {::generate generate-button}
   fo/field-styles   {::m.c.wallet-addresses/wallet :pick-one}
   fo/field-options
   {::m.c.wallet-addresses/wallet
    {::picker-options/query-key       ::m.c.wallets/index
     ::picker-options/query-component u.links/WalletLinkForm
     ::picker-options/options-xform
     (fn [_ options]
       (mapv
        (fn [{::m.c.wallets/keys [id name]}]
          {:text  (str name)
           :value [::m.c.wallets/id id]})
        (sort-by ::m.c.wallets/name options)))}}
   fo/route-prefix   "wallet-address"
   fo/title          "Wallet Address"})

(def new-action-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewWalletAddressForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.wallet-addresses/path-index
                        m.c.wallet-addresses/address]
   ro/controls         {::m.c.wallets/id {:type :uuid :label "id"}
                        ::new     new-action-button
                        ::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/field-formatters {::m.c.wallet-addresses/wallet #(u.links/ui-wallet-link %2)}
   ro/form-links       {::m.c.wallet-addresses/address WalletAddressForm}
   ro/route            "wallets-addresses"
   ro/row-actions      [generate-button]
   ro/row-pk           m.c.wallet-addresses/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.c.wallet-addresses/index-by-wallet
   ro/title            "Addresses"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [::m.c.wallets/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.wallets/id nil
                       :ui/report       {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/info :SubPage/creating {:props props})
  (ui-report report))

(def ui-sub-page (comp/factory SubPage))
