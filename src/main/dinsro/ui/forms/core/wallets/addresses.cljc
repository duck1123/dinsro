(ns dinsro.ui.forms.core.wallets.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.mutations.core.wallet-addresses :as mu.c.wallet-addresses]
   [dinsro.options.core.wallet-addresses :as o.c.wallet-addresses]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

(def model-key o.c.wallet-addresses/id)

(def generate-button
  {:type   :button
   :local? true
   :label  "Generate"
   :action (fn [this props]
             (let [{::m.c.wallet-addresses/keys [id]} props]
               (log/info :generate-button/clicked {:props props})
               (comp/transact! this [`(mu.c.wallet-addresses/generate! {~model-key ~id})])))})

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes    [m.c.wallet-addresses/address
                     m.c.wallet-addresses/wallet]
   fo/field-styles  {o.c.wallet-addresses/wallet :pick-one}
   fo/field-options {o.c.wallet-addresses/wallet u.pickers/admin-wallet-picker}
   fo/id            m.c.wallet-addresses/id
   fo/route-prefix  "new-wallet-address"
   fo/title         "New Wallet Address"})

(form/defsc-form WalletAddressForm
  [_this _props]
  {fo/action-buttons [::generate]
   fo/attributes     [m.c.wallet-addresses/address
                      m.c.wallet-addresses/wallet]
   fo/controls       {::generate generate-button}
   fo/field-styles   {o.c.wallet-addresses/wallet :pick-one}
   fo/field-options  {o.c.wallet-addresses/wallet u.pickers/wallet-picker}
   fo/id             m.c.wallet-addresses/id
   fo/route-prefix   "wallet-address"
   fo/title          "Wallet Address"})
