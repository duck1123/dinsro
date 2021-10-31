(ns dinsro.ui.ln-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]
   [dinsro.model.joins :as m.joins]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.ln-info :as m.ln-info]
   [dinsro.model.ln-peers :as m.ln-peers]
   [dinsro.model.ln-transactions :as m.ln-tx]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.ln-nodes :as mu.ln]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(declare LightningNodeForm)

(defn form-control
  [label mutation]
  {:label  label
   :type   :button
   :local? true
   :action (fn [this _keyword]
             (let [{::m.ln-nodes/keys [id]} (comp/props this)]
               (comp/transact! this [(mutation {::m.ln-nodes/id id})])
               (report/run-report! this)))})

(def form-connect-button
  {:type   :button
   :local? true
   :label  "Connect"
   :action (fn [this _] (form/create! this LightningNodeForm))})

(def update-info-button
  {:label  "Update Info"
   :type   :button
   :action (fn [this _keyword]
             (let [{::m.ln-nodes/keys [id]} (comp/props this)]
               (if id
                 (comp/transact! this [(mu.ln/update-info! {::m.ln-nodes/id id})])
                 (log/error "no id"))))})

(def new-node-button
  {:type   :button
   :local? true
   :label  "New Node"
   :action (fn [this _] (form/create! this LightningNodeForm))})

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

(def override-form false)

(def button-info
  [["Fetch Peers" mu.ln/fetch-peers!]
   ["Fetch Channels" mu.ln/fetch-channels!]
   ["Fetch Transactions" mu.ln/fetch-transactions!]
   ["Download Cert" mu.ln/download-cert!]
   ["Download Macaroon" mu.ln/download-macaroon!]
   ["initialize" mu.ln/initialize!]
   ["unlock" mu.ln/unlock!]
   ["update info" mu.ln/update-info!]
   ["fetch address" mu.ln/fetch-address!]
   ["generate" mu.ln/generate!]])

(form/defsc-form PeerSubform
  [_this _props]
  {fo/id         m.ln-peers/id
   fo/attributes [m.ln-peers/address]})

(def override-tx-subform false)

(form/defsc-form TxSubform
  [this {::m.ln-tx/keys [id amount]
         :as            props}]
  {fo/id            m.ln-tx/id
   fo/layout-styles {:form-container      :table1
                     :form-body-container :table2
                     :ref-container       :table3}
   fo/attributes    [m.ln-tx/tx-hash
                     m.ln-tx/amount
                     m.ln-tx/label
                     m.ln-tx/block-height
                     m.ln-tx/time-stamp]}
  (if override-tx-subform
    (form/render-layout this props)
    (dom/div :.ui.container
      (dom/p {} "Tx: " (str id))
      (dom/p {} "Amount: " amount))))

(defsc ActionsMenuItem
  [this {:keys [label mutation id]}]
  (ui-dropdown-item
   {:text    label
    :onClick #(comp/transact! this [(mutation {::m.ln-nodes/id id})])}))

(def ui-actions-menu-item (comp/factory ActionsMenuItem {:keyfn :label}))

(defsc ActionsMenu
  [_this {::m.ln-nodes/keys [id]}]
  {:initial-state {::m.ln-nodes/id nil}
   :query         [::m.ln-nodes/id]}
  (ui-dropdown
   {:icon    "settings"
    :button  true
    :labeled false}
   (ui-dropdown-menu
    {}
    (for [[label mutation] button-info]
      (ui-actions-menu-item {:label label :mutation mutation :id id})))))

(def ui-actions-menu (comp/factory ActionsMenu))

(form/defsc-form LightningNodeForm
  [this props]
  {fo/id            m.ln-nodes/id
   fo/attributes    [m.ln-nodes/name
                     m.ln-info/alias-attr
                     m.ln-info/identity-pubkey
                     m.joins/ln-node-transactions]
   fo/subforms      {::m.ln-nodes/peers        {fo/ui PeerSubform}
                     ::m.ln-nodes/transactions {fo/ui TxSubform}}
   fo/layout-styles {::m.ln-nodes/transactions :tableh}
   fo/field-styles  {::m.ln-nodes/transactions :ln-tx-row}
   fo/controls      (merge form/standard-controls
                           {::connect            form-connect-button
                            ::update-info        update-info-button
                            ::init               (form-control "init"   mu.ln/initialize!)
                            ::unlock-button      (form-control "unlock" mu.ln/unlock!)
                            ::download-cert!     (form-control "->C"    mu.ln/download-cert!)
                            ::download-macaroon! (form-control "->M"    mu.ln/download-macaroon!)})
   fo/cancel-route  ["ln-nodes"]
   fo/route-prefix  "ln-node"
   fo/title         "Lightning Node"}
  (let [node-id (::m.ln-nodes/id props)]
    (if override-form
      (form/render-layout this props)
      (dom/div :.ui.grid
        (dom/div :.row
          (dom/div :.sixteen.wide.column
            (dom/div :.ui.container
              (ui-actions-menu {::m.ln-nodes/id node-id}))))
        (dom/div :.row
          (dom/div :.sixteen.wide.column
            (dom/div {}
              (form/render-layout this props))))))))

(defattr node-user-link ::m.ln-nodes/user :ref
  {ao/cardinality      :one
   ao/identities       #{::m.ln-nodes/id}
   ao/target           ::m.users/id
   ::report/column-EQL {::m.ln-nodes/user (comp/get-query u.links/UserLink)}})

(report/defsc-report LightningNodesReport
  [_this _props]
  {ro/columns          [m.ln-nodes/name
                        m.ln-info/alias-attr
                        m.ln-info/color
                        node-user-link]
   ro/control-layout   {:action-buttons [::new-node]}
   ro/controls         {::new-node new-node-button}
   ro/field-formatters
   {::m.ln-nodes/user (fn [_this props] (u.links/ui-user-link props))}
   ro/machine          lightning-node-report-machine
   ro/route            "ln-nodes"
   ro/row-pk           m.ln-nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln-nodes/all-nodes
   ro/title            "Lightning Node Report"})
