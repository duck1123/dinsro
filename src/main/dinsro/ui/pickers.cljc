(ns dinsro.ui.pickers
  (:require
   [com.fulcrologic.rad.picker-options :as picker-options]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.joins.ln.remote-nodes :as j.ln.remote-nodes]
   [dinsro.joins.users :as j.users]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(defn record-xform
  [model-key display-key]
  (fn [this options]
    (log/info :account-xform/starting {:options options :this this})
    (mapv (fn [props]
            (let [id      (get props model-key)
                  display (get props display-key)
                  ident   [model-key id]]
              {:text display :value ident}))
          (sort-by display-key options))))

(defn account-xform
  [this options]
  (log/info :account-xform/starting {:options options :this this})
  (let [model-key   ::m.accounts/id
        display-key ::m.accounts/name]
    (mapv (fn [props]
            (let [id      (get props model-key)
                  display (get props display-key)
                  ident   [model-key id]]
              {:text display :value ident}))
          (sort-by display-key options))))

(def account-picker
  {::picker-options/query-key       ::j.accounts/flat-index
   ::picker-options/query-component u.links/AccountLinkForm
   ::picker-options/options-xform   (record-xform ::m.accounts/id ::m.accounts/name)})

(def admin-account-picker
  {::picker-options/query-key       ::j.accounts/flat-admin-index
   ::picker-options/query-component u.links/AccountLinkForm
   ::picker-options/options-xform   (record-xform ::m.accounts/id ::m.accounts/name)})

(def admin-core-node-picker
  {::picker-options/query-key       ::j.c.nodes/admin-flat-index
   ::picker-options/query-component u.links/AdminCoreNodeLinkForm
   ::picker-options/options-xform   (record-xform ::m.c.nodes/id ::m.c.nodes/name)})

(def admin-currency-picker
  {::picker-options/query-key       ::j.currencies/flat-admin-index
   ::picker-options/query-component u.links/CurrencyLinkForm
   ::picker-options/options-xform   (record-xform ::m.currencies/id ::m.currencies/name)})

(def admin-ln-node-picker
  {::picker-options/query-key       ::j.ln.nodes/admin-flat-index
   ::picker-options/query-component u.links/AdminLnNodeLinkForm
   ::picker-options/options-xform   (record-xform ::m.ln.nodes/id ::m.ln.nodes/name)})

(def admin-remote-node-picker
  {::picker-options/query-key       ::j.ln.remote-nodes/admin-flat-index
   ::picker-options/query-component u.links/AdminRemoteNodeLinkForm
   ::picker-options/options-xform   (record-xform ::m.ln.remote-nodes/id ::m.ln.remote-nodes/pubkey)})

(def admin-user-picker
  {::picker-options/query-key       ::j.users/admin-flat-index
   ::picker-options/query-component u.links/UserLinkForm
   ::picker-options/options-xform   (record-xform ::m.users/id ::m.users/name)})

(def admin-wallet-picker
  {::picker-options/query-key       ::j.c.wallets/admin-flat-index
   ::picker-options/query-component u.links/WalletLinkForm
   ::picker-options/options-xform   (record-xform ::m.c.wallets/id ::m.c.wallets/name)})

(def core-node-picker
  {::picker-options/query-key       ::j.c.nodes/flat-index
   ::picker-options/query-component u.links/CoreNodeLinkForm
   ::picker-options/options-xform   (record-xform ::m.c.nodes/id ::m.c.nodes/name)})

(def currency-picker
  {::picker-options/query-key       ::j.currencies/flat-index
   ::picker-options/query-component u.links/CurrencyLinkForm
   ::picker-options/options-xform   (record-xform ::m.currencies/id ::m.currencies/name)})

(def ln-node-picker
  {::picker-options/query-key       ::j.ln.nodes/flat-index
   ::picker-options/query-component u.links/NodeLinkForm
   ::picker-options/options-xform   (record-xform ::m.ln.nodes/id ::m.ln.nodes/name)})

(def network-picker
  {::picker-options/query-key       ::j.c.nodes/flat-index
   ::picker-options/query-component u.links/NetworkLinkForm
   ::picker-options/options-xform   (record-xform ::m.c.networks/id ::m.c.networks/name)})

(def node-picker
  {::picker-options/query-key       ::j.c.nodes/flat-index
   ::picker-options/query-component u.links/CoreNodeLinkForm
   ::picker-options/options-xform
   (fn [_ options]
     (mapv
      (fn [{::m.c.nodes/keys [id name]}]
        {:text  (str name)
         :value [::m.c.nodes/id id]})
      (sort-by ::m.c.nodes/name options)))})

(def remote-node-picker
  {::picker-options/query-key       ::j.ln.remote-nodes/flat-index
   ::picker-options/query-component u.links/RemoteNodeLinkForm
   ::picker-options/options-xform   (record-xform ::m.ln.remote-nodes/id ::m.ln.remote-nodes/pubkey)})

(def user-picker
  {::picker-options/query-key       ::j.users/flat-index
   ::picker-options/query-component u.links/UserLinkForm
   ::picker-options/options-xform
   (fn [_ options]
     (mapv
      (fn [{::m.users/keys [id name]}]
        {:text  (str name)
         :value [::m.users/id id]})
      (sort-by ::m.users/name options)))})

(def wallet-picker
  {::picker-options/query-key       ::j.c.wallets/flat-index
   ::picker-options/query-component u.links/WalletLinkForm
   ::picker-options/options-xform
   (fn [_ options]
     (mapv
      (fn [{::m.c.wallets/keys [id name]}]
        {:text  (str name)
         :value [::m.c.wallets/id id]})
      (sort-by ::m.c.wallets/name options)))})
