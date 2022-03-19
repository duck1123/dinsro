(ns dinsro.ui.core.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.core.wallets :as mu.c.wallets]
   [dinsro.ui.links :as u.links]))

(defsc RefRow
  [_this props]
  {:ident ::m.c.wallets/id
   :query [::m.c.wallets/id
           ::m.c.wallets/name]}
  (dom/tr {}
    (dom/td (u.links/ui-rate-link props))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.c.wallets/id}))

(defn ref-table
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "Name")))
     (dom/tbody {}
       (for [ref value]
         (ui-ref-row ref))))))

(def render-ref-table (render-field-factory ref-table))

(def create-button
  {:type   :button
   :local? true
   :label  "Create"
   :action (fn [this _]
             (let [props (comp/props this)]
               (comp/transact! this [(mu.c.wallets/create! props)])))})

(def override-new-wallet-form false)

(form/defsc-form NewWalletForm [_this _props]
  {fo/id             m.c.wallets/id
   fo/action-buttons (concat [::create] form/standard-action-buttons)
   fo/attributes     [m.c.wallets/name
                      m.c.wallets/node
                      m.c.wallets/user]

   fo/controls       (merge form/standard-controls {::create create-button})
   fo/field-styles   {::m.c.wallets/node :pick-one
                      ::m.c.wallets/user :pick-one}
   fo/field-options
   {::m.c.wallets/node
    {::picker-options/query-key       ::m.c.nodes/index
     ::picker-options/query-component u.links/CoreNodeLinkForm
     ::picker-options/options-xform
     (fn [_ options]
       (mapv
        (fn [{::m.c.nodes/keys [id name]}]
          {:text  (str name)
           :value [::m.c.nodes/id id]})
        (sort-by ::m.c.nodes/name options)))}
    ::m.c.wallets/user
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

(def roll-button
  {:type   :button
   :local? true
   :label  "Roll"
   :action (fn [this _key]
             (let [{::m.c.wallets/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.c.wallets/roll! {::m.c.wallets/id id})])
               #_(form/view! this CoreBlockForm id)))})

(form/defsc-form WalletForm [_this _props]
  {fo/id             m.c.wallets/id
   fo/action-buttons (concat [::roll] form/standard-action-buttons)
   fo/attributes     [m.c.wallets/id
                      m.c.wallets/name
                      m.c.wallets/node
                      m.c.wallets/derivation
                      m.c.wallets/key
                      ;; m.c.wallets/seed
                      ;; j.c.wallets/addresses
                      j.c.wallets/words]
   fo/controls       (merge form/standard-controls {::roll roll-button})
   fo/field-styles   {::m.c.wallets/addresses :link-list
                      ::m.c.wallets/words     :word-list}
   fo/route-prefix   "wallet"
   fo/subforms       {::m.c.wallets/node      {fo/ui u.links/CoreNodeLinkForm}
                      ::m.c.wallets/addresses {fo/ui u.links/WalletAddressLinkForm}
                      ::m.c.wallets/words     {fo/ui u.links/WordLinkForm}}
   fo/title          "Wallet"})

(def delete-action-button
  {:type   :button
   :local? true
   :label  "Delete"
   :action (fn [this {::m.c.wallets/keys [id]}]
             (form/delete! this ::m.c.wallets/id id))})

(def new-action-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewWalletForm))})

(report/defsc-report WalletReport
  [_this _props]
  {ro/columns          [m.c.wallets/name
                        m.c.wallets/node
                        m.c.wallets/user]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-action-button}
   ro/field-formatters {::m.c.wallets/node #(u.links/ui-core-node-link %2)
                        ::m.c.wallets/user #(u.links/ui-user-link %2)}
   ro/form-links       {::m.c.wallets/name WalletForm}
   ro/route            "wallets"
   ro/row-actions      [delete-action-button]
   ro/row-pk           m.c.wallets/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.c.wallets/index
   ro/title            "Wallet Report"})
