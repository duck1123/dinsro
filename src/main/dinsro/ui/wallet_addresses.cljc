(ns dinsro.ui.wallet-addresses
  (:require
   #?(:cljs [com.fulcrologic.fulcro.components :as comp])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.wallets :as m.wallets]
   [dinsro.model.wallet-addresses :as m.wallet-addresses]
   #?(:cljs [dinsro.mutations.wallet-addresses :as mu.wallet-addresses])
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(form/defsc-form NewWalletAddressForm
  [_this _props]
  {fo/id         m.wallet-addresses/id
   fo/attributes [m.wallet-addresses/address

                  m.wallet-addresses/wallet]
   fo/field-styles {::m.wallet-addresses/wallet :pick-one}
   fo/field-options
   {::m.wallet-addresses/wallet
    {::picker-options/query-key       ::m.wallets/index
     ::picker-options/query-component u.links/WalletLinkForm
     ::picker-options/options-xform
     (fn [_ options]
       (mapv
        (fn [{::m.wallets/keys [id name]}]
          {:text  (str name)
           :value [::m.wallets/id id]})
        (sort-by ::m.wallets/name options)))}}
   fo/route-prefix "new-wallet-address"
   fo/title        "New Wallet Address"})

(def generate-button
  {:type   :button
   :local? true
   :label  "Generate"
   :action (fn [this _]
             (comment this)
             #?(:cljs
                (let [props                            (comp/props this)
                      {::m.wallet-addresses/keys [id]} props]
                  (comp/transact! this [(mu.wallet-addresses/generate! {::m.wallet-addresses/id id})]))))})

(form/defsc-form WalletAddressForm
  [_this _props]
  {fo/id             m.wallet-addresses/id
   fo/action-buttons [::generate]
   fo/attributes     [m.wallet-addresses/address
                      m.wallet-addresses/wallet]
   fo/controls       {::generate generate-button}
   fo/field-styles   {::m.wallet-addresses/wallet :pick-one}
   fo/field-options
   {::m.wallet-addresses/wallet
    {::picker-options/query-key       ::m.wallets/index
     ::picker-options/query-component u.links/WalletLinkForm
     ::picker-options/options-xform
     (fn [_ options]
       (mapv
        (fn [{::m.wallets/keys [id name]}]
          {:text  (str name)
           :value [::m.wallets/id id]})
        (sort-by ::m.wallets/name options)))}}
   fo/route-prefix   "wallet-address"
   fo/title          "Wallet Address"})

(def new-action-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewWalletAddressForm))})

(report/defsc-report WalletAddressesReport
  [_this _props]
  {ro/columns          [m.wallet-addresses/address
                        m.wallet-addresses/wallet]
   ro/controls         {::new new-action-button}
   ro/control-layout   {:action-buttons [::new]}
   ro/field-formatters {::m.wallet-addresses/wallet #(u.links/ui-wallet-link %2)}
   ro/form-links       {::m.wallet-addresses/address WalletAddressForm}
   ro/route            "wallets-addresses"
   ro/row-actions      []
   ro/row-pk           m.wallet-addresses/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.wallet-addresses/index
   ro/title            "Wallet Addresses Report"})
