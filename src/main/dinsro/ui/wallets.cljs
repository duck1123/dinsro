(ns dinsro.ui.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.wallets :as j.wallets]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.users :as m.users]
   [dinsro.model.wallets :as m.wallets]
   [dinsro.mutations.wallets :as mu.wallets]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]))

(def create-button
  {:type   :button
   :local? true
   :label  "Create"
   :action (fn [this _]
             (let [props (comp/props this)]
               (comp/transact! this [(mu.wallets/create! props)])))})

(def override-new-wallet-form false)

(form/defsc-form NewWalletForm [_this _props]
  {fo/id             m.wallets/id
   fo/action-buttons (concat [::create] form/standard-action-buttons)
   fo/attributes     [m.wallets/name
                      m.wallets/node
                      m.wallets/user]
   fo/controls       (merge form/standard-controls {::create create-button})
   fo/field-styles   {::m.wallets/node :pick-one
                      ::m.wallets/user :pick-one}
   fo/field-options
   {::m.wallets/node
    {::picker-options/query-key       ::m.core-nodes/index
     ::picker-options/query-component u.links/CoreNodeLinkForm
     ::picker-options/options-xform
     (fn [_ options]
       (mapv
        (fn [{::m.core-nodes/keys [id name]}]
          {:text  (str name)
           :value [::m.core-nodes/id id]})
        (sort-by ::m.core-nodes/name options)))}
    ::m.wallets/user
    {::picker-options/query-key       ::m.users/index
     ::picker-options/query-component u.links/UserLinkForm
     ::picker-options/options-xform
     (fn [_ options]
       (mapv
        (fn [{::m.users/keys [id name]}]
          {:text  (str name)
           :value [::m.users/id id]})
        (sort-by ::m.users/name options)))}}
   fo/route-prefix   "new-wallet"
   fo/title          "New Wallet"})

(form/defsc-form WalletForm [_this _props]
  {fo/id           m.wallets/id
   fo/attributes   [m.wallets/id
                    m.wallets/name
                    m.wallets/node
                    j.wallets/addresses]
   fo/field-styles {::m.wallets/addresses :link-list}
   fo/route-prefix "wallet"
   fo/subforms     {::m.wallets/node      {fo/ui u.links/CoreNodeLinkForm}
                    ::m.wallets/addresses {fo/ui u.links/WalletAddressLinkForm}}
   fo/title        "Wallet"})

(def delete-action-button
  {:type   :button
   :local? true
   :label  "Delete"
   :action (fn [this {::m.wallets/keys [id]}]
             (form/delete! this ::m.wallets/id id))})

(def new-action-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewWalletForm))})

(report/defsc-report WalletReport
  [_this _props]
  {ro/columns          [m.wallets/name
                        m.wallets/node
                        m.wallets/user]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-action-button}
   ro/field-formatters {::m.wallets/node #(u.links/ui-core-node-link %2)
                        ::m.wallets/user #(u.links/ui-user-link %2)}
   ro/form-links       {::m.wallets/name WalletForm}
   ro/route            "wallets"
   ro/row-actions      [delete-action-button]
   ro/row-pk           m.wallets/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.wallets/index
   ro/title            "Wallet Report"})
