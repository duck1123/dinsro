(ns dinsro.ui.core.nodes
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.container :as container :refer [defsc-container]]
   [com.fulcrologic.rad.container-options :as co]
   [com.fulcrologic.rad.control-options :as copt]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.core.node-blocks :as u.c.node-blocks]
   [dinsro.ui.core.node-peers :as u.c.node-peers]
   [dinsro.ui.core.node-transactions :as u.c.node-transactions]
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
   {:label  "generate"
    :action mu.c.nodes/generate!}])

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

(def show-peers true)
(def show-blocks true)
(def show-transactions false)

(defsc ShowNode
  "Show a core node"
  [this {::m.c.nodes/keys [id name network]
         :ui/keys         [blocks peers transactions]
         :as              props}]
  {:route-segment ["node" :id]
   :query         [::m.c.nodes/id
                   ::m.c.nodes/name
                   {::m.c.nodes/network (comp/get-query u.links/NetworkLinkForm)}
                   {:ui/peers (comp/get-query u.c.node-peers/SubPage)}
                   {:ui/blocks (comp/get-query u.c.node-blocks/SubPage)}
                   {:ui/transactions (comp/get-query u.c.node-transactions/SubPage)}
                   #_[df/marker-table '_]]
   :initial-state {::m.c.nodes/id      nil
                   ::m.c.nodes/name    ""
                   ::m.c.nodes/network {}
                   :ui/peers           {}
                   :ui/blocks          {}
                   :ui/transactions    {}}
   :ident         ::m.c.nodes/id
   :pre-merge     (u.links/page-merger
                   ::m.c.nodes/id
                   {:ui/blocks       u.c.node-blocks/SubPage
                    :ui/peers        u.c.node-peers/SubPage
                    :ui/transactions u.c.node-transactions/SubPage})
   :will-enter    (partial u.links/page-loader ::m.c.nodes/id ::ShowNode)}
  (log/finer :ShowNode/creating {:id id :props props :this this})
  (let [{:keys [main sub]} (css/get-classnames ShowNode)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (ui-actions-menu {::m.c.nodes/id id})
        (dom/dl {}
          (dom/dt {} "Name")
          (dom/dd {} (str name))
          (dom/dt {} "Network")
          (dom/dd {} (u.links/ui-network-link network)))
        (u.links/log-props props))
      (when id
        (dom/div {:classes [sub]}
          (when show-peers
            (if peers
              (u.c.node-peers/ui-sub-page peers)
              (dom/p :.ui.segment "Peers not defined")))
          (when show-blocks
            (if blocks
              (u.c.node-blocks/ui-sub-page blocks)
              (dom/p :.ui.segment "Blocks not defined")))
          (when show-transactions (u.c.node-transactions/ui-sub-page transactions)))))))

(form/defsc-form NewCoreNodeForm [_this _props]
  {fo/id           m.c.nodes/id
   fo/attributes   [m.c.nodes/name
                    m.c.nodes/host
                    m.c.nodes/port
                    m.c.nodes/rpcuser
                    m.c.nodes/rpcpass]
   fo/cancel-route ["nodes"]
   fo/route-prefix "new-core-node"
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
                         m.c.nodes/host
                         m.c.nodes/network]
   ro/column-formatters {::m.c.nodes/name    #(u.links/ui-core-node-link %3)
                         ::m.c.nodes/network #(u.links/ui-network-link %2)}
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
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
   copt/controls       {::refresh u.links/refresh-control}
   copt/control-layout {:action-buttons [::refresh]}})
