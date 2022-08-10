(ns dinsro.ui.core.wallets
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.core.wallets :as mu.c.wallets]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.core.wallet-addresses :as u.c.wallet-addresses]
   [dinsro.ui.core.wallet-words :as u.c.wallet-words]
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
                      m.c.wallets/node
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

(form/defsc-form WalletForm
  [this props]
  {fo/id             m.c.wallets/id
   fo/action-buttons (concat [::roll] form/standard-action-buttons)
   fo/attributes     [m.c.wallets/id
                      m.c.wallets/name
                      m.c.wallets/node
                      m.c.wallets/derivation
                      m.c.wallets/key
                      j.c.wallets/words]
   fo/controls       (merge form/standard-controls {::roll roll-button})
   fo/field-styles   {::m.c.wallets/addresses :link-list
                      ::m.c.wallets/words     :word-list}
   fo/route-prefix   "wallet"
   fo/subforms       {::m.c.wallets/node      {fo/ui u.links/CoreNodeLinkForm}
                      ::m.c.wallets/addresses {fo/ui u.links/WalletAddressLinkForm}
                      ::m.c.wallets/words     {fo/ui u.links/WordLinkForm}}
   fo/title          "Wallet"}
  (log/info :WalletForm/creating {:props props})
  (form/render-layout this props))

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

(defn ShowWallet-pre-merge
  [{:keys [data-tree state-map current-normalized]}]
  (log/finer :ShowTransaction-pre-merge/starting
             {:data-tree          data-tree
              :state-map          state-map
              :current-noramlized current-normalized})
  (let [id (::m.c.wallets/id data-tree)]
    (log/finer :ShowTransaction-pre-merge/parsed {:id id})
    (let [addresses-data
          (let [initial (comp/get-initial-state u.c.wallet-addresses/WalletAddressesSubPage)
                state   (get-in state-map (comp/get-ident u.c.wallet-addresses/WalletAddressesSubPage {}))
                merged  (merge initial state {::m.c.wallets/id id})]
            (log/info :ShowBlock-pre-merge/address-data {:initial initial :state state :merged merged})
            merged)
          words-data
          (let [initial (comp/get-initial-state u.c.wallet-words/WalletWordsSubPage)
                state   (get-in state-map (comp/get-ident u.c.wallet-words/WalletWordsSubPage {}))
                merged  (merge initial state {::m.c.wallets/id id})]
            (log/info :ShowBlock-pre-merge/words-data {:initial initial :state state :merged merged})
            merged)
          updated-data (-> data-tree
                           (assoc :ui/addresses addresses-data)
                           (assoc :ui/words words-data))]
      (log/finer :ShowBlock-pre-merge/merged
                 {:updated-data       updated-data
                  :data-tree          data-tree
                  :state-map          state-map
                  :current-noramlized current-normalized})
      updated-data)))

(defsc ShowWallet
  "Show a wallet"
  [this {::m.c.wallets/keys [id name derivation key node]
         :ui/keys           [addresses words]
         :as                props}]
  {:route-segment ["wallets" :id]
   :query         [::m.c.wallets/id
                   ::m.c.wallets/name
                   ::m.c.wallets/derivation
                   {::m.c.wallets/node (comp/get-query u.links/CoreNodeLinkForm)}
                   ::m.c.wallets/key
                   {:ui/addresses (comp/get-query u.c.wallet-addresses/WalletAddressesSubPage)}
                   {:ui/words (comp/get-query u.c.wallet-words/WalletWordsSubPage)}
                   [df/marker-table '_]]
   :initial-state {::m.c.wallets/id         nil
                   ::m.c.wallets/name       ""
                   ::m.c.wallets/derivation ""
                   ::m.c.wallets/key        ""
                   ::m.c.wallets/node       {}
                   :ui/addresses            {}
                   :ui/words                {}}
   :ident         ::m.c.wallets/id
   :will-enter
   (fn [app {id :id}]
     (let [id    (new-uuid id)
           ident [::m.c.wallets/id id]
           state (-> (app/current-state app) (get-in ident))]
       (log/info :ShowWallet/will-enter {:id id :app app})
       (dr/route-deferred
        ident
        (fn []
          (log/info :ShowWallet/will-enter2 {:id id :state state})
          (df/load!
           app ident ShowWallet
           {:marker               :ui/selected-node
            :target               [:ui/selected-node]
            :post-mutation        `dr/target-ready
            :post-mutation-params {:target ident}})))))
   :pre-merge     ShowWallet-pre-merge}
  (log/info :ShowWallet/creating {:id id :props props :this this})
  (dom/div {}
    (dom/div :.ui.segment
      (dom/h1 {} "Wallet")
      (dom/button
        {:onClick (fn [_]
                    (log/info :ShowWallet/derive-clicked {})
                    (comp/transact! this [(mu.c.wallets/derive! {::m.c.wallets/id id})]))}
        "derive")
      (dom/p :.ui.segment "Name: " (str name))
      (dom/p :.ui.segment "Derivation: " (str derivation))
      (dom/p :.ui.segment "Key: " (str key))
      (dom/p :.ui.segment "Node: " (u.links/ui-core-node-link node)))
    (if id
      (comp/fragment
       (dom/div :.ui.segment
         (u.c.wallet-addresses/ui-wallet-addresses-sub-page addresses))
       (dom/div :.ui.segment
         (u.c.wallet-words/ui-wallet-words-sub-page words)))

      (dom/p {} "id not set"))))

(report/defsc-report WalletsReport
  [this props]
  {ro/columns          [m.c.wallets/name
                        m.c.wallets/node
                        m.c.wallets/user]
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
