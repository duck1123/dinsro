(ns dinsro.ui.core.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.core.wallets :as mu.c.wallets]
   [dinsro.ui.core.wallet-accounts :as u.c.wallet-accounts]
   [dinsro.ui.core.wallet-addresses :as u.c.wallet-addresses]
   [dinsro.ui.core.wallet-words :as u.c.wallet-words]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

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

(form/defsc-form NewWalletForm [this props]
  {fo/id             m.c.wallets/id
   fo/action-buttons (concat [::create] form/standard-action-buttons)
   fo/attributes     [m.c.wallets/name
                      m.c.wallets/user]

   fo/controls     (merge form/standard-controls {::create create-button})
   fo/field-styles {::m.c.wallets/node :pick-one
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
   fo/route-prefix "new-wallet"
   fo/title        "New Wallet"}
  (log/info :NewWalletForm/creating {:props props})
  (form/render-layout this props))

(defn render-word-list
  [_this props]
  (log/info :render-word-list/creating {:props props})
  (dom/div {}
    "word list"))

(def roll-button
  {:type   :button
   :local? true
   :label  "Roll"
   :action (fn [this _key]
             (let [{::m.c.wallets/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.c.wallets/roll! {::m.c.wallets/id id})])))})

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

(defsc ShowWallet
  "Show a wallet"
  [this {::m.c.wallets/keys [id name derivation key network user
                             ext-public-key ext-private-key]
         :ui/keys           [addresses words accounts]
         :as                props}]
  {:route-segment ["wallets" :id]
   :query         [::m.c.wallets/id
                   ::m.c.wallets/name
                   ::m.c.wallets/derivation
                   {::m.c.wallets/network (comp/get-query u.links/NetworkLinkForm)}
                   {::m.c.wallets/user (comp/get-query u.links/UserLinkForm)}
                   ::m.c.wallets/key
                   ::m.c.wallets/ext-private-key
                   ::m.c.wallets/ext-public-key
                   {:ui/accounts (comp/get-query u.c.wallet-accounts/SubPage)}
                   {:ui/addresses (comp/get-query u.c.wallet-addresses/SubPage)}
                   {:ui/words (comp/get-query u.c.wallet-words/SubPage)}
                   [df/marker-table '_]]
   :initial-state {::m.c.wallets/id         nil
                   ::m.c.wallets/name       ""
                   ::m.c.wallets/derivation ""
                   ::m.c.wallets/key        ""
                   ::m.c.wallets/ext-private-key ""
                   ::m.c.wallets/ext-public-key ""
                   ::m.c.wallets/network    {}
                   ::m.c.wallets/user       {}
                   :ui/addresses            {}
                   :ui/words                {}}
   :ident         ::m.c.wallets/id
   :pre-merge     (u.links/page-merger
                   ::m.c.wallets/id
                   {:ui/accounts     u.c.wallet-accounts/SubPage
                    :ui/addresses u.c.wallet-addresses/SubPage
                    :ui/words     u.c.wallet-words/SubPage})
   :will-enter    (partial u.links/page-loader ::m.c.wallets/id ::ShowWallet)}
  (log/info :ShowWallet/creating {:id id :props props :this this})
  (dom/div {}
    (dom/div :.ui.segment
      (dom/h1 {} "Wallet")
      (dom/button
        {:onClick (fn [_]
                    (log/info :ShowWallet/derive-clicked {})
                    (comp/transact! this [(mu.c.wallets/derive! {::m.c.wallets/id id})]))}
        "derive")
      (dom/dl {}
        (dom/dt {} "Name")
        (dom/dd {} (str name))
        (dom/dt {} "Derivation")
        (dom/dd {} (str derivation))
        (dom/dt {} "Key")
        (dom/dd {} (str key))
        (dom/dt {} "Network")
        (dom/dd {} (u.links/ui-network-link network))
        (dom/dt {} "User")
        (dom/dd {} (u.links/ui-user-link user))
        (dom/dt {} "Extended Public Key")
        (dom/dd {} ext-public-key)
        (dom/dt {} "Extended Private Key")
        (dom/dd {} ext-private-key)))

    (if id
      (comp/fragment
       (dom/div :.ui.segment
         (u.c.wallet-words/ui-sub-page words))
       (dom/div :.ui.segment
         (u.c.wallet-accounts/ui-sub-page accounts))
       (dom/div :.ui.segment
         (u.c.wallet-addresses/ui-sub-page addresses)))

      (dom/p {} "id not set"))))

(report/defsc-report WalletsReport
  [this props]
  {ro/columns          [m.c.wallets/name
                        m.c.wallets/user
                        m.c.wallets/derivation
                        m.c.wallets/ext-public-key
                        m.c.wallets/ext-private-key]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-action-button}
   ro/field-formatters {::m.c.wallets/node #(u.links/ui-core-node-link %2)
                        ::m.c.wallets/name (u.links/report-link ::m.c.wallets/name u.links/ui-wallet-link)
                        ::m.c.wallets/user #(u.links/ui-user-link %2)}
   ro/route            "wallets"
   ro/row-actions      [delete-action-button]
   ro/row-pk           m.c.wallets/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.c.wallets/index
   ro/title            "Wallet Report"}
  (log/info :WalletsReport/creating {:props props})
  (report/render-layout this))

(def ui-wallets-report (comp/factory WalletsReport))
