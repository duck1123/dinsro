(ns dinsro.ui.core.nodes
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.control-options :as copt]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.container :as container :refer [defsc-container]]
   [com.fulcrologic.rad.container-options :as co]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [dinsro.ui.core.peers :as u.c.peers]
   [dinsro.ui.core.tx :as u.c.tx]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

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
   fo/route-prefix "node-block"
   fo/attributes   [m.c.blocks/hash m.c.blocks/height]})

(form/defsc-form CoreNodeTxSubform
  [_this _props]
  {fo/id           m.c.tx/id
   fo/title        "Core Node Transactions"
   fo/attributes   [m.c.tx/hex m.c.tx/version m.c.tx/block]
   fo/route-prefix "node-tx"
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
                   component                    (comp/registry-key->class :dinsro.ui.core.wallets/NewWalletForm)
                   state                        {::m.c.wallets/name "new wallet"
                                                 ::m.c.wallets/node {::m.c.nodes/id   id
                                                                     ::m.c.nodes/name name}}
                   options                      {:initial-state state}]
               (form/create! this component options)))})

(def new-peer-button
  {:type   :button
   :local? true
   :label  "New Peer"
   :action (fn [this _]
             (let [{::m.c.nodes/keys [id name]} (comp/props this)
                   component                    (comp/registry-key->class :dinsro.ui.core.peers/NewCorePeerForm)
                   state                        {::m.c.nodes/node {::m.c.nodes/id   id
                                                                   ::m.c.nodes/name name}}
                   options                      {:initial-state state}]
               (form/create! this component options)))})

(def override-form false)

(def button-info
  [{:label  "fetch"
    :action mu.c.nodes/fetch!}
   {:label  "fetch peers"
    :action mu.c.nodes/fetch-peers!}
   {:label "generate"
    :action mu.c.nodes/generate!}])

(form/defsc-form CoreNodeForm [this props]
  {fo/id             m.c.nodes/id
   fo/action-buttons [::fetch
                      ::fetch-peers
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
   fo/cancel-route   ["nodes"]
   fo/controls       {::fetch       fetch-button
                      ::fetch-peers fetch-peers-button
                      ::generate    generate-button
                      ::new-wallet  new-wallet-button
                      ::new-peer    new-peer-button}
   fo/field-styles   {::m.c.nodes/blocks       :core-block-table
                      ::m.c.nodes/transactions :link-list
                      ::m.c.nodes/ln-nodes     :link-list
                      ::m.c.nodes/wallets      :link-list
                      ::m.c.nodes/peers        :link-list}
   fo/route-prefix   "node3"
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
      (form/render-layout this props)
      (u.c.peers/ui-peers-report {}))))

(defsc ActionsMenuItem
  [this {:keys [label mutation id]}]
  (ui-dropdown-item
   {:text    label
    :onClick #(comp/transact! this [(mutation {::m.c.nodes/id id})])}))

(def ui-actions-menu-item (comp/factory ActionsMenuItem {:keyfn :label}))

(defsc ActionsMenu
  [_this {::m.c.nodes/keys [id]}]
  {:initial-state {::m.c.nodes/id nil}
   :query         [::m.c.nodes/id]}
  (ui-dropdown
   {:icon    "settings"
    :button  true
    :labeled false}
   (ui-dropdown-menu
    {}
    (for [{:keys [label action]} button-info]
      (ui-actions-menu-item {:label label :mutation action :id id})))))

(def ui-actions-menu
  "node actions menu"
  (comp/factory ActionsMenu))

(defsc ShowNode
  "Show a core node"
  [this {::m.c.nodes/keys [id name]
         :keys            [blocks peers tx
                           wallets]
         :as              props}]
  {:route-segment ["node" :id]
   :query         [::m.c.nodes/id
                   ::m.c.nodes/name
                   {:peers (comp/get-query u.c.peers/CorePeersReport)}
                   {:blocks (comp/get-query u.c.blocks/CoreBlockReport)}
                   {:tx (comp/get-query u.c.tx/CoreTxReport)}
                   {:wallets (comp/get-query u.c.wallets/WalletReport)}
                   [df/marker-table '_]]
   :initial-state {::m.c.nodes/id   nil
                   ::m.c.nodes/name ""
                   :peers           {}
                   :blocks          {}
                   :tx              {}
                   :wallets         {}}
   :ident         ::m.c.nodes/id
   :will-enter
   (fn [app {id :id}]
     (log/info :node/will-show {:app app :id id})
     (let [id      (new-uuid id)
           ident   [::m.c.nodes/id id]
           state   (-> (app/current-state app) (get-in ident))
           invoice (-> state :organization/latest-invoice)]
       (if invoice
         (dr/route-immediate ident)
         (dr/route-deferred
          ident
          (fn []
            (log/info :nodes/will-enter {:id       id
                                         :state    state
                                         :controls (control/component-controls app)})
            (report/start-report! app u.c.peers/CorePeersReport {:route-params {::m.c.nodes/id id}})
            (report/start-report! app u.c.blocks/CoreBlockReport {:route-params {::m.c.blocks/node id}})
            (report/start-report! app u.c.tx/CoreTxReport {:route-params {::m.c.tx/node id}})
            (report/start-report! app u.c.wallets/WalletReport {:route-params {::m.c.wallets/node id}})
            (log/info :nodes/will-enter2 {:id       id
                                          :state    state
                                          :controls (control/component-controls app)})
            (df/load!
             app ident ShowNode
             {:marker               :ui/selected-node
              :target               [:ui/selected-node]
              :post-mutation        `dr/target-ready
              :post-mutation-params {:target ident}}))))))
   :pre-merge
   (fn [{:keys [data-tree state-map]}]
     (log/info :node/pre-merge {})
     (let [initial             (comp/get-initial-state u.c.peers/CorePeersReport)
           report-data         (get-in state-map (comp/get-ident u.c.peers/CorePeersReport {}))
           updated-report-data (merge initial report-data)
           initial-block-data  (comp/get-initial-state u.c.blocks/CoreBlockReport)
           block-data          (get-in state-map (comp/get-ident u.c.blocks/CoreBlockReport {}))
           updated-block-data  (merge initial-block-data block-data)
           initial-tx-data     (comp/get-initial-state u.c.tx/CoreTxReport)
           transaction-data    (get-in state-map (comp/get-ident u.c.tx/CoreTxReport {}))
           updated-tx-data     (merge initial-tx-data transaction-data)
           initial-wallet-data (comp/get-initial-state u.c.wallets/WalletReport)
           wallet-data         (get-in state-map (comp/get-ident u.c.wallets/WalletReport))
           updated-wallet-data (merge initial-wallet-data wallet-data)
           updated-data        (-> data-tree
                                   (assoc :peers updated-report-data)
                                   (assoc :blocks updated-block-data)
                                   (assoc :tx updated-tx-data)
                                   (assoc :wallets updated-wallet-data))]
       (log/info :node/merged {:updated-data updated-data})
       updated-data))}
  (log/info :nodes/show {:props props :this this})
  (dom/div {}
    (ui-actions-menu {::m.c.nodes/id id})
    (dom/h1 {} (str id))
    (dom/p {} "name" (str name))
    (when id
      (log/info :params/merging {:id id :peers peers})
      (comp/fragment
       (let [peer-data (assoc-in peers [:ui/parameters ::m.c.nodes/id] id)]
         (log/info :peer-report/running {:peer-data peer-data})
         (u.c.peers/ui-peers-report peer-data))
       (let [blocks-data (assoc-in blocks [:ui/parameters ::m.c.blocks/node] id)]
         (log/info :block-report/running {:blocks-data blocks-data})
         (u.c.blocks/ui-blocks-report blocks-data))
       (let [transactions-data (assoc-in tx [:ui/parameters ::m.c.tx/node] id)]
         (log/info :block-report/running {:transactions-data transactions-data})
         (u.c.tx/ui-tx-report transactions-data))
       (let [wallet-data (assoc-in wallets [:ui/parameters ::m.c.wallets/node] id)]
         (log/info :wallet-report/running {:wallet-data wallet-data})
         (u.c.wallets/ui-wallet-report wallet-data))))))

(form/defsc-form NewCoreNodeForm [_this _props]
  {fo/id           m.c.nodes/id
   fo/attributes   [m.c.nodes/name
                    m.c.nodes/host
                    m.c.nodes/port
                    m.c.nodes/rpcuser
                    m.c.nodes/rpcpass]
   fo/cancel-route ["nodes"]
   fo/route-prefix "node2"
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
             (log/info :class/calculating {})
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
   ro/route             "nodes"})

(defsc-container NodeContainer
  [_this _props]
  {co/children         {:node  ShowNode
                        :nodes CoreNodesReport}
   co/layout           [[{:id :nodes :width 16}]
                        [{:id :node :width 16}]]
   co/route            "node-container"
   co/title            "Node"
   copt/controls       {::refresh {:type   :button
                                   :label  "Refresh"
                                   :action (fn [container] (control/run! container))}}
   copt/control-layout {:action-buttons [::refresh]
                        ;; these inputs are pulled up from nested reports (any control that is not marked local will be)
                        ;; :inputs         [[:start-date :end-date]]
                        }})
