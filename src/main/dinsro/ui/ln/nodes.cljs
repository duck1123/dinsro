(ns dinsro.ui.ln.nodes
  (:require
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
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.model.core.nodes :as m.core-nodes]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.ln.transactions :as m.ln.tx]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.ln.nodes :as mu.ln]
   [dinsro.ui.ln.channels :as u.ln.channels]
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
   fo/route-prefix "ln-nodes-tx"
   fo/title        "Transactions"
   fo/attributes   [m.ln.tx/tx-hash]})

(def override-tx-subform true)

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
                     {::picker-options/query-key       ::m.core-nodes/index
                      ::picker-options/query-component u.links/CoreNodeLinkForm
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.core-nodes/keys [id name]}]
                           {:text  (str name)
                            :value [::m.core-nodes/id id]})
                         (sort-by ::m.core-nodes/name options)))}
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
   fo/cancel-route  ["ln-nodes"]
   fo/route-prefix  "create-ln-node"
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
                      ;; j.ln.nodes/transactions
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
   fo/cancel-route   ["ln-nodes"]
   fo/route-prefix   "ln-node"
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

(report/defsc-report LightningNodesReport
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
   ro/route            "ln-nodes"
   ro/row-pk           m.ln.nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.nodes/index
   ro/title            "Lightning Node Report"})

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
   ro/route            "ln-nodes"
   ro/row-pk           m.ln.nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.nodes/admin-index
   ro/title            "Lightning Node Report"})
