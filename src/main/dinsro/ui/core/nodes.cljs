(ns dinsro.ui.core.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.nodes :as j.core-nodes]
   [dinsro.model.core.blocks :as m.core-block]
   [dinsro.model.core.nodes :as m.core-nodes]
   [dinsro.model.core.tx :as m.core-tx]
   [dinsro.model.core.wallets :as m.wallets]
   [dinsro.mutations.core.nodes :as mu.core-nodes]
   [dinsro.ui.core.blocks :as u.core-block]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log2]))

(defn connect-action
  [report-instance {::m.core-nodes/keys [id]}]
  (comp/transact! report-instance [(mu.core-nodes/connect! {::m.core-nodes/id id})]))

(defn fetch-action
  [report-instance {::m.core-nodes/keys [id]}]
  (comp/transact! report-instance [(mu.core-nodes/fetch! {::m.core-nodes/id id})]))

(defn delete-action
  [report-instance {::m.core-nodes/keys [id]}]
  (form/delete! report-instance ::m.core-nodes/id id))

(def connect-button
  {:label     "Connect"
   :action    connect-action
   :disabled? (fn [_ row-props] (:account/active? row-props))})

(form/defsc-form CoreNodeBlockSubform
  [_this _props]
  {fo/id           m.core-block/id
   fo/title        "Blocks"
   fo/route-prefix "core-node-block"
   fo/attributes   [m.core-block/hash m.core-block/height]})

(form/defsc-form CoreNodeTxSubform
  [_this _props]
  {fo/id           m.core-tx/id
   fo/title        "Core Node Transactions"
   fo/attributes   [m.core-tx/hex m.core-tx/version m.core-tx/block]
   fo/route-prefix "core-node-tx"
   fo/subforms     {::m.core-tx/block {fo/ui CoreNodeBlockSubform}}})

(def fetch-button
  {:type   :button
   :local? true
   :label  "Fetch"
   :action (fn [this _]
             (let [{::m.core-nodes/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.core-nodes/fetch! {::m.core-nodes/id id})])))})

(def fetch-peers-button
  {:type   :button
   :local? true
   :label  "Fetch Peers"
   :action (fn [this _]
             (let [{::m.core-nodes/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.core-nodes/fetch-peers! {::m.core-nodes/id id})])))})

(def generate-button
  {:type   :button
   :local? true
   :label  "Generate"
   :action (fn [this _]
             (let [{::m.core-nodes/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.core-nodes/generate! {::m.core-nodes/id id})])))})

(def new-wallet-button
  {:type   :button
   :local? true
   :label  "New Wallet"
   :action (fn [this _]
             (let [{::m.core-nodes/keys [id name]} (comp/props this)
                   component                       (comp/registry-key->class :dinsro.ui.wallets/NewWalletForm)
                   state                           {::m.wallets/name "new wallet"
                                                    ::m.wallets/node {::m.core-nodes/id   id
                                                                      ::m.core-nodes/name name}}
                   options                         {:initial-state state}]
               (form/create! this component options)))})

(def new-peer-button
  {:type   :button
   :local? true
   :label  "New Peer"
   :action (fn [this _]
             (let [{::m.core-nodes/keys [id name]} (comp/props this)
                   component                       (comp/registry-key->class :dinsro.ui.core-peers/NewCorePeerForm)
                   state                           {::m.core-nodes/node {::m.core-nodes/id   id
                                                                         ::m.core-nodes/name name}}
                   options                         {:initial-state state}]
               (form/create! this component options)))})

(def override-form true)

(form/defsc-form CoreNodeForm [this props]
  {fo/id             m.core-nodes/id
   fo/action-buttons [::fetch
                      ::fetch-peer
                      ::generate
                      ::new-wallet
                      ::new-peer]
   fo/attributes     [m.core-nodes/name
                      m.core-nodes/chain
                      m.core-nodes/block-count
                      j.core-nodes/blocks
                      j.core-nodes/transactions
                      j.core-nodes/ln-nodes
                      j.core-nodes/wallets
                      j.core-nodes/peers]
   fo/cancel-route   ["core-nodes"]
   fo/controls       {::fetch       fetch-button
                      ::fetch-peers fetch-peers-button
                      ::generate generate-button
                      ::new-wallet  new-wallet-button
                      ::new-peer    new-peer-button}
   fo/field-styles   {::m.core-nodes/blocks       :core-block-table
                      ::m.core-nodes/transactions :link-list
                      ::m.core-nodes/ln-nodes     :link-list
                      ::m.core-nodes/wallets      :link-list
                      ::m.core-nodes/peers        :link-list}
   fo/route-prefix   "core-node"
   fo/subforms       {::m.core-nodes/transactions {fo/ui CoreNodeTxSubform}
                      ::m.core-nodes/blocks       {fo/ui u.core-block/CoreBlockSubForm}
                      ::m.core-nodes/ln-nodes     {fo/ui u.links/NodeLinkForm}
                      ::m.core-nodes/wallets      {fo/ui u.links/WalletLinkForm}
                      ::m.core-nodes/peers        {fo/ui u.links/CorePeerLinkForm}}
   fo/title          "Core Node"}
  (if override-form
    (form/render-layout this props)
    (dom/div :.ui.container
      (dom/h1 {} "Core Node")
      (form/render-layout this props))))

(form/defsc-form NewCoreNodeForm [_this _props]
  {fo/id           m.core-nodes/id
   fo/attributes   [m.core-nodes/name
                    m.core-nodes/host
                    m.core-nodes/port
                    m.core-nodes/rpcuser
                    m.core-nodes/rpcpass]
   fo/cancel-route ["core-nodes"]
   fo/route-prefix "core-node2"
   fo/subforms     {::m.core-nodes/transactions {fo/ui CoreNodeTxSubform}}
   fo/title        "Core Node"})

(def fetch-action-button
  {:label     "Fetch"
   :action    fetch-action
   :disabled? (fn [_ row-props] (:account/active? row-props))})

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button
   :class  (fn []
             (log2/info :class/calculating {})
             "red")})

(def new-button
  {:type   :button
   :local? true
   :label  "New Node"
   :action (fn [this _] (form/create! this NewCoreNodeForm))})

(report/defsc-report CoreNodesReport
  [_this _props]
  {ro/columns           [m.core-nodes/name
                         m.core-nodes/chain]
   ro/column-formatters {::m.core-nodes/name #(u.links/ui-core-node-link %3)}
   ro/control-layout    {:action-buttons [::new]}
   ro/controls          {::new new-button}
   ro/row-actions       [fetch-action-button delete-action-button]
   ro/source-attribute  ::m.core-nodes/index
   ro/title             "Core Node Report"
   ro/row-pk            m.core-nodes/id
   ro/run-on-mount?     true
   ro/route             "core-nodes"})
