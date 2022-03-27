(ns dinsro.ui.core.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.wallets :as m.wallets]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log2]))

(defn connect-action
  [report-instance {::m.c.nodes/keys [id]}]
  (comp/transact! report-instance [(mu.c.nodes/connect! {::m.c.nodes/id id})]))

(defn fetch-action
  [report-instance {::m.c.nodes/keys [id]}]
  (comp/transact! report-instance [(mu.c.nodes/fetch! {::m.c.nodes/id id})]))

(defn delete-action
  [report-instance {::m.c.nodes/keys [id]}]
  (form/delete! report-instance ::m.c.nodes/id id))

(def connect-button
  {:label     "Connect"
   :action    connect-action
   :disabled? (fn [_ row-props] (:account/active? row-props))})

(form/defsc-form CoreNodeBlockSubform
  [_this _props]
  {fo/id           m.c.blocks/id
   fo/title        "Blocks"
   fo/route-prefix "core-node-block"
   fo/attributes   [m.c.blocks/hash m.c.blocks/height]})

(form/defsc-form CoreNodeTxSubform
  [_this _props]
  {fo/id           m.c.tx/id
   fo/title        "Core Node Transactions"
   fo/attributes   [m.c.tx/hex m.c.tx/version m.c.tx/block]
   fo/route-prefix "core-node-tx"
   fo/subforms     {::m.c.tx/block {fo/ui CoreNodeBlockSubform}}})

(def fetch-button
  {:type   :button
   :local? true
   :label  "Fetch"
   :action (fn [this _]
             (let [{::m.c.nodes/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.c.nodes/fetch! {::m.c.nodes/id id})])))})

(def fetch-peers-button
  {:type   :button
   :local? true
   :label  "Fetch Peers"
   :action (fn [this _]
             (let [{::m.c.nodes/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.c.nodes/fetch-peers! {::m.c.nodes/id id})])))})

(def generate-button
  {:type   :button
   :local? true
   :label  "Generate"
   :action (fn [this _]
             (let [{::m.c.nodes/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.c.nodes/generate! {::m.c.nodes/id id})])))})

(def new-wallet-button
  {:type   :button
   :local? true
   :label  "New Wallet"
   :action (fn [this _]
             (let [{::m.c.nodes/keys [id name]} (comp/props this)
                   component                       (comp/registry-key->class :dinsro.ui.wallets/NewWalletForm)
                   state                           {::m.wallets/name "new wallet"
                                                    ::m.wallets/node {::m.c.nodes/id   id
                                                                      ::m.c.nodes/name name}}
                   options                         {:initial-state state}]
               (form/create! this component options)))})

(def new-peer-button
  {:type   :button
   :local? true
   :label  "New Peer"
   :action (fn [this _]
             (let [{::m.c.nodes/keys [id name]} (comp/props this)
                   component                       (comp/registry-key->class :dinsro.ui.c.peers/NewCorePeerForm)
                   state                           {::m.c.nodes/node {::m.c.nodes/id   id
                                                                      ::m.c.nodes/name name}}
                   options                         {:initial-state state}]
               (form/create! this component options)))})

(def override-form true)

(form/defsc-form CoreNodeForm [this props]
  {fo/id             m.c.nodes/id
   fo/action-buttons [::fetch
                      ::fetch-peer
                      ::generate
                      ::new-wallet
                      ::new-peer]
   fo/attributes     [m.c.nodes/name
                      m.c.nodes/chain
                      m.c.nodes/block-count
                      j.c.nodes/blocks
                      j.c.nodes/transactions
                      j.c.nodes/ln-nodes
                      j.c.nodes/wallets
                      j.c.nodes/peers]
   fo/cancel-route   ["core-nodes"]
   fo/controls       {::fetch       fetch-button
                      ::fetch-peers fetch-peers-button
                      ::generate generate-button
                      ::new-wallet  new-wallet-button
                      ::new-peer    new-peer-button}
   fo/field-styles   {::m.c.nodes/blocks       :core-block-table
                      ::m.c.nodes/transactions :link-list
                      ::m.c.nodes/ln-nodes     :link-list
                      ::m.c.nodes/wallets      :link-list
                      ::m.c.nodes/peers        :link-list}
   fo/route-prefix   "core-node"
   fo/subforms       {::m.c.nodes/transactions {fo/ui CoreNodeTxSubform}
                      ::m.c.nodes/blocks       {fo/ui u.c.blocks/CoreBlockSubForm}
                      ::m.c.nodes/ln-nodes     {fo/ui u.links/NodeLinkForm}
                      ::m.c.nodes/wallets      {fo/ui u.links/WalletLinkForm}
                      ::m.c.nodes/peers        {fo/ui u.links/CorePeerLinkForm}}
   fo/title          "Core Node"}
  (if override-form
    (form/render-layout this props)
    (dom/div :.ui.container
      (dom/h1 {} "Core Node")
      (form/render-layout this props))))

(form/defsc-form NewCoreNodeForm [_this _props]
  {fo/id           m.c.nodes/id
   fo/attributes   [m.c.nodes/name
                    m.c.nodes/host
                    m.c.nodes/port
                    m.c.nodes/rpcuser
                    m.c.nodes/rpcpass]
   fo/cancel-route ["core-nodes"]
   fo/route-prefix "core-node2"
   fo/subforms     {::m.c.nodes/transactions {fo/ui CoreNodeTxSubform}}
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
  {ro/columns           [m.c.nodes/name
                         m.c.nodes/chain]
   ro/column-formatters {::m.c.nodes/name #(u.links/ui-core-node-link %3)}
   ro/control-layout    {:action-buttons [::new]}
   ro/controls          {::new new-button}
   ro/row-actions       [fetch-action-button delete-action-button]
   ro/source-attribute  ::m.c.nodes/index
   ro/title             "Core Node Report"
   ro/row-pk            m.c.nodes/id
   ro/run-on-mount?     true
   ro/route             "core-nodes"})
