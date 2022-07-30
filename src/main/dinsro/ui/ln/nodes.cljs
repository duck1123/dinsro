(ns dinsro.ui.ln.nodes
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.ln.transactions :as m.ln.tx]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.ln.nodes :as mu.ln]
   [dinsro.ui.ln.channels :as u.ln.channels]
   [dinsro.ui.ln.node-channels :as u.ln.node-channels]
   [dinsro.ui.ln.node-peers :as u.ln.node-peers]
   [dinsro.ui.ln.node-transactions :as u.ln.node-transactions]
   [dinsro.ui.ln.payreqs :as u.ln.payreqs]
   [dinsro.ui.ln.peers :as u.ln.peers]
   [dinsro.ui.ln.invoices :as u.ln.invoices]
   [dinsro.ui.ln.payments :as u.ln.payments]
   [dinsro.ui.ln.transactions :as u.ln.tx]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(declare CreateLightningNodeForm)

(def new-node-button
  {:type   :button
   :local? true
   :label  "New Node"
   :action (fn [this _] (form/create! this CreateLightningNodeForm))})

(def new-invoice-button
  {:type   :button
   :local? true
   :label  "New Invoice"
   :action
   (fn [this _kw]
     (let [{::m.ln.nodes/keys [id name]} (comp/props this)
           component                     (comp/registry-key->class :dinsro.ui.ln.invoices/NewInvoiceForm)
           node                          {::m.ln.nodes/id   id
                                          ::m.ln.nodes/name name}
           state                         {::m.ln.invoices/memo "This is a memo"
                                          ::m.ln.invoices/node node}
           options                       {:initial-state state}]
       (form/create! this component options)))})

(def new-payment-button
  {:type   :button
   :local? true
   :label  "New Payment"
   :action
   (fn [this _kw]
     (let [{::m.ln.nodes/keys [id name]} (comp/props this)
           component                     (comp/registry-key->class :dinsro.ui.ln.payreqs/NewPaymentForm)
           node                          {::m.ln.nodes/id   id
                                          ::m.ln.nodes/name name}
           state                         {::m.ln.payreqs/memo "This is a memo"
                                          ::m.ln.payreqs/node node}
           options                       {:initial-state state}]
       (form/create! this component options)))})

(defn edit-detail
  [])

(uism/defstatemachine lightning-node-report-machine
  (-> report/report-machine
      (update-in
       [::uism/states :state/gathering-parameters ::uism/events]
       assoc
       :event/edit-detail
       {::uism/handler
        (fn [_env]
          (log/info "editing detail")
          (edit-detail))})))

(def button-info
  [{:label            "Fetch Peers"
    :action           mu.ln/fetch-peers!
    :requiresCert     true
    :requiresMacaroon true}
   {:label            "Fetch Channels"
    :action           mu.ln/fetch-channels!
    :requiresCert     true
    :requiresMacaroon true}
   {:label            "Fetch Invoices"
    :action           mu.ln/fetch-invoices!
    :requiresCert     true
    :requiresMacaroon true}
   {:label            "Fetch Payments"
    :action           mu.ln/fetch-payments!
    :requiresCert     true
    :requiresMacaroon true}
   {:label            "Fetch Transactions"
    :action           mu.ln/fetch-transactions!
    :requiresCert     true
    :requiresMacaroon true}
   {:label            "Download Cert"
    :action           mu.ln/download-cert!
    :hideCert         true
    :requiresCert     false
    :requiresMacaroon false}
   {:label            "Download Macaroon"
    :action           mu.ln/download-macaroon!
    :hideMacaroon     true
    :requiresCert     true
    :requiresMacaroon false}
   {:label            "Initialize"
    :action           mu.ln/initialize!
    :requiresCert     true
    :requiresMacaroon false}
   {:label            "Unlock"
    :action           mu.ln/unlock!
    :requiresCert     true
    :requiresMacaroon true}
   {:label            "Update Info"
    :action           mu.ln/update-info!
    :requiresCert     true
    :requiresMacaroon true}
   {:label            "Generate"
    :action           mu.ln/generate!
    :requiresCert     true
    :requiresMacaroon true}])

(form/defsc-form CoreTxForm
  [_this _props]
  {fo/id           m.ln.tx/tx-hash
   fo/route-prefix "nodes-tx"
   fo/title        "Transactions"
   fo/attributes   [m.ln.tx/tx-hash]})

(defsc ActionsMenuItem
  [this {:keys [label mutation id]}]
  (ui-dropdown-item
   {:text    label
    :onClick #(comp/transact! this [(mutation {::m.ln.nodes/id id})])}))

(def ui-actions-menu-item (comp/factory ActionsMenuItem {:keyfn :label}))

(defsc ActionsMenu
  [_this {::m.ln.nodes/keys [id hasCert? hasMacaroon?]}]
  {:initial-state {::m.ln.nodes/id           nil
                   ::m.ln.nodes/hasCert?     false
                   ::m.ln.nodes/hasMacaroon? false}
   :query         [::m.ln.nodes/id ::m.ln.nodes/hasCert?
                   ::m.ln.nodes/hasMacaroon?]}
  (ui-dropdown
   {:icon    "settings"
    :button  true
    :labeled false}
   (ui-dropdown-menu
    {}
    (for [{:keys [label action hideCert hideMacaroon requiresCert requiresMacaroon]} button-info]
      (let [cert-hidden     (and hasCert? hideCert)
            macaroon-hidden (and hasMacaroon? hideMacaroon)
            hidden          (or cert-hidden macaroon-hidden)]
        (when (not (or hidden
                       (and requiresCert (not hasCert?))
                       (and requiresMacaroon (not hasMacaroon?))))
          (ui-actions-menu-item {:label label :mutation action :id id})))))))

(def ui-actions-menu (comp/factory ActionsMenu))

(def override-create-form false)

(form/defsc-form CreateLightningNodeForm
  [this props]
  {fo/id            m.ln.nodes/id
   fo/attributes    [m.ln.nodes/name
                     m.ln.nodes/host
                     m.ln.nodes/port
                     m.ln.nodes/core-node
                     m.ln.nodes/user]
   fo/field-options {::m.ln.nodes/core-node
                     {::picker-options/query-key       ::m.c.nodes/index
                      ::picker-options/query-component u.links/CoreNodeLinkForm
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.c.nodes/keys [id name]}]
                           {:text  (str name)
                            :value [::m.c.nodes/id id]})
                         (sort-by ::m.c.nodes/name options)))}
                     ::m.ln.nodes/user
                     {::picker-options/query-key       ::m.users/index
                      ::picker-options/query-component u.links/UserLinkForm
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.users/keys [id name]}]
                           {:text  (str name)
                            :value [::m.users/id id]})
                         (sort-by ::m.users/name options)))}}
   fo/field-styles  {::m.ln.nodes/core-node :pick-one
                     ::m.ln.nodes/user      :pick-one}
   fo/cancel-route  ["nodes"]
   fo/route-prefix  "create-node"
   fo/title         "Create Lightning Node"}
  (if override-create-form
    (form/render-layout this props)
    (dom/div :.ui.grid
      (dom/div :.row
        (dom/div :.sixteen.wide.column
          (dom/div {}
            (form/render-layout this props)))))))

(def override-form false)

(form/defsc-form LightningNodeForm
  [this {::m.ln.nodes/keys [id hasCert? hasMacaroon?] :as props}]
  {fo/id             m.ln.nodes/id
   fo/attributes     [m.ln.nodes/name
                      m.ln.nodes/core-node
                      m.ln.info/alias-attr
                      m.ln.nodes/hasCert?
                      m.ln.nodes/hasMacaroon?
                      m.ln.info/identity-pubkey
                      j.ln.nodes/peers
                      j.ln.nodes/channels
                      j.ln.nodes/invoices
                      j.ln.nodes/payreqs
                      j.ln.nodes/payments]
   fo/action-buttons [::new-invoice ::new-payment]
   fo/controls       {::new-invoice new-invoice-button
                      ::new-payment new-payment-button}
   fo/subforms       {::m.ln.nodes/core-node    {fo/ui u.links/CoreNodeLinkForm}
                      ::m.ln.nodes/payments     {fo/ui u.ln.payments/PaymentSubForm}
                      ::m.ln.nodes/payreqs      {fo/ui u.ln.payreqs/PayreqSubForm}
                      ::m.ln.nodes/peers        {fo/ui u.ln.peers/PeerSubform}
                      ::m.ln.nodes/transactions {fo/ui u.ln.tx/TxSubform}
                      ::m.ln.nodes/channels     {fo/ui u.ln.channels/ChannelSubform}
                      ::m.ln.nodes/invoices     {fo/ui u.ln.invoices/InvoiceSubForm}}
   fo/field-styles   {::m.ln.nodes/core-node    :link
                      ::m.ln.nodes/channels     :ln-channels-row
                      ::m.ln.nodes/payments     :ln-payments-row
                      ::m.ln.nodes/payreqs      :ln-payreqs-row
                      ::m.ln.nodes/peers        :ln-peer-row
                      ::m.ln.nodes/transactions :ln-tx-row
                      ::m.ln.nodes/invoices     :ln-invoice-row}
   fo/cancel-route   ["nodes"]
   fo/route-prefix   "node"
   fo/title          "Lightning Node"}
  (if override-form
    (form/render-layout this props)
    (dom/div :.ui.grid
      (dom/div :.row
        (dom/div :.sixteen.wide.column
          (dom/div :.ui
            (ui-actions-menu
             {::m.ln.nodes/id           id
              ::m.ln.nodes/hasCert?     hasCert?
              ::m.ln.nodes/hasMacaroon? hasMacaroon?}))))
      (dom/div :.row
        (dom/div :.sixteen.wide.column
          (dom/div {}
            (form/render-layout this props)))))))

(defn ShowNode-pre-merge
  [{:keys [data-tree state-map current-normalized]}]
  (log/info :ShowNode/pre-merge {:data-tree          data-tree
                                 :state-map          state-map
                                 :current-noramlized current-normalized})
  (let [node-id (::m.ln.nodes/id data-tree)]
    (log/info :ShowNode/pre-merge-parsed {:node-id node-id})
    (let [peers-data
          (merge
           (comp/get-initial-state u.ln.node-peers/NodePeersSubPage)
           (get-in state-map (comp/get-ident u.ln.node-peers/NodePeersSubPage {}))
           {::m.ln.nodes/id node-id})

          channels-data
          (merge
           (comp/get-initial-state u.ln.node-channels/NodeChannelsSubPage)
           (get-in state-map (comp/get-ident u.ln.node-channels/NodeChannelsSubPage {}))
           {::m.ln.nodes/id node-id})

          transactions-data
          (merge
           (comp/get-initial-state u.ln.node-transactions/NodeTransactionsSubPage)
           (get-in state-map (comp/get-ident u.ln.node-transactions/NodeTransactionsSubPage {}))
           {::m.ln.nodes/id node-id})

          updated-data (-> data-tree
                           (assoc :peers peers-data)
                           (assoc :channels channels-data)
                           (assoc :transactions transactions-data))]
      (log/info :ShowNode/merged {:updated-data       updated-data
                                  :data-tree          data-tree
                                  :state-map          state-map
                                  :current-noramlized current-normalized})
      updated-data)))

(defsc ShowNode
  "Show a ln node"
  [this {:keys             [peers channels transactions]
         ::m.ln.info/keys  [chains]
         ::m.ln.nodes/keys [id user core-node host port hasCert? hasMacaroon?]}]
  {:route-segment ["nodes" :id]
   :query         [{:channels (comp/get-query u.ln.node-channels/NodeChannelsSubPage)}
                   {:peers (comp/get-query u.ln.node-peers/NodePeersSubPage)}
                   {:transactions (comp/get-query u.ln.node-transactions/NodeTransactionsSubPage)}
                   ::m.ln.nodes/id
                   ::m.ln.nodes/host
                   ::m.ln.nodes/port
                   ::m.ln.nodes/hasCert?
                   ::m.ln.nodes/hasMacaroon?
                   ::m.ln.info/chains
                   {::m.ln.nodes/user (comp/get-query u.links/UserLinkForm)}
                   {::m.ln.nodes/core-node (comp/get-query u.links/CoreNodeLinkForm)}]
   :initial-state {:channels                 {}
                   :peers                    {}
                   :transactions             {}
                   ::m.ln.info/chains        []
                   ::m.ln.nodes/id           nil
                   ::m.ln.nodes/user         {}
                   ::m.ln.nodes/core-node    {}
                   ::m.ln.nodes/host         ""
                   ::m.ln.nodes/port         0
                   ::m.ln.nodes/hasCert?     false
                   ::m.ln.nodes/hasMacaroon? false}
   :ident         ::m.ln.nodes/id
   :pre-merge     ShowNode-pre-merge
   :will-enter
   (fn [app {id :id}]
     (let [id    (new-uuid id)
           ident [::m.ln.nodes/id id]
           state (-> (app/current-state app) (get-in ident))]
       (log/info :ShowNode/will-enter {:app app :id id :ident ident})
       (dr/route-immediate ident)
       (dr/route-deferred
        ident
        (fn []
          (log/info :ShowNode/will-enter2 {:id       id
                                           :state    state
                                           :controls (control/component-controls app)})
          (df/load!
           app ident ShowNode
           {:marker               :ui/selected-node
            :target               [:ui/selected-node]
            :post-mutation        `dr/target-ready
            :post-mutation-params {:target ident}})))))}

  (dom/div {}
    (dom/div :.ui.segment
      (ui-actions-menu
       {::m.ln.nodes/id           id
        ::m.ln.nodes/hasCert?     hasCert?
        ::m.ln.nodes/hasMacaroon? hasMacaroon?})
      (dom/p {} "User: " (u.links/ui-user-link user))
      (dom/p {} "Core Node: " (u.links/ui-core-node-link core-node))
      (dom/p {} "Address: " host ":" (str port))
      (dom/p {} "Chains: " (pr-str chains))
      (when-not hasCert?
        (comp/fragment
         (dom/p {} "Cert not found")
         (dom/button {:classes [:.ui.button]
                      :onClick #(comp/transact! this [(mu.ln/download-cert! {::m.ln.nodes/id id})])}
           "Fetch")))
      (dom/p {} "Has Cert: " (str hasCert?))
      (dom/p {}
        "Has Macaroon: "
        (if hasMacaroon?
          (str hasMacaroon?)
          (dom/a {:onClick #(comp/transact! this [(mu.ln/download-macaroon! {::m.ln.nodes/id id})])}
            (str hasMacaroon?))))
      (dom/p {} (pr-str id)))
    (u.ln.node-peers/ui-node-peers-sub-page peers)
    (u.ln.node-channels/ui-node-channels-sub-page channels)
    (u.ln.node-transactions/ui-node-transactions-sub-page transactions)))

(report/defsc-report LightningNodesReport
  [this props]
  {ro/columns
   [m.ln.nodes/id
    m.ln.info/alias-attr
    m.ln.nodes/core-node
    m.ln.info/color
    m.ln.nodes/user]

   ro/control-layout
   {:action-buttons [::new-node ::refresh]}

   ro/controls
   {::new-node new-node-button
    ::refresh
    {:type   :button
     :label  "Refresh"
     :action (fn [this] (control/run! this))}}

   ro/field-formatters
   {::m.ln.nodes/id
    (fn [this id]
      (let [props2 (comp/props this)]
        (log/info :LightningNodesReport/formatting-name {:id id :props2 props2})
        (let [{:ui/keys [current-rows]} props2
              row                       (first (filter
                                                (fn [r]
                                                  (= (::m.ln.nodes/id r) id))
                                                current-rows))]
          (if row
            (do
              (log/info :LightningNodesReport/row {:row row})
              (let [name (::m.ln.info/alias row)]
                (u.links/ui-node-link
                 {::m.ln.nodes/id   id
                  ::m.ln.nodes/name name})))
            (dom/p {} "not found")))))

    ::m.ln.nodes/user
    (fn [_this props]
      (log/info :LightningNodesReport/formatting-user {:props props})
      (u.links/ui-user-link props))

    ::m.ln.nodes/core-node
    (fn [_this props] (u.links/ui-core-node-link props))}
   ro/route            "nodes"
   ro/row-pk           m.ln.nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.nodes/index
   ro/title            "Lightning Node Report"}
  (do
    (log/info :LightningNodesReport/starting {:props props})
    (report/render-layout this)))

(report/defsc-report LNNodesSubReport
  [_this _props]
  {ro/columns          [m.ln.nodes/name
                        m.ln.info/alias-attr
                        m.ln.nodes/core-node
                        m.ln.info/color]
   ro/control-layout   {:action-buttons [::new-node]}
   ro/form-links       {::m.ln.nodes/name LightningNodeForm}
   ro/controls         {::new-node new-node-button}
   ro/field-formatters {::m.ln.nodes/user      (fn [_this props] (u.links/ui-user-link props))
                        ::m.ln.nodes/core-node (fn [_this props] (u.links/ui-core-node-link props))}
   ro/machine          lightning-node-report-machine
   ro/row-pk           m.ln.nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.nodes/index
   ro/title            "Lightning Node Report"})

(report/defsc-report AdminLNNodesReport
  [_this _props]
  {ro/columns          [m.ln.nodes/name
                        m.ln.info/alias-attr
                        m.ln.nodes/core-node
                        m.ln.info/color
                        m.ln.nodes/user]
   ro/control-layout   {:action-buttons [::new-node]}
   ro/form-links       {::m.ln.nodes/name LightningNodeForm}
   ro/controls         {::new-node new-node-button}
   ro/field-formatters {::m.ln.nodes/user      (fn [_this props] (u.links/ui-user-link props))
                        ::m.ln.nodes/core-node (fn [_this props] (u.links/ui-core-node-link props))}
   ro/machine          lightning-node-report-machine
   ro/route            "nodes"
   ro/row-pk           m.ln.nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.nodes/admin-index
   ro/title            "Lightning Node Report"})
