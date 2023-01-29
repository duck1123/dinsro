(ns dinsro.ui.links
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.routing :as rroute]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [lambdaisland.glogc :as log]))

(defn form-link
  [this id name form-kw]
  (log/debug :form-link/starting {:id id :name name :form-kw form-kw})
  (dom/a {:href "#"
          :data-form (str form-kw)
          :onClick
          (fn [e]
            (.preventDefault e)
            (if-let [component (comp/registry-key->class form-kw)]
              (form/view! this component id)
              (log/error :form-link/no-component {:form-kw form-kw})))}
    name))

(defn get-control-value
  [report-instance id-key]
  (some->> report-instance comp/props
           :ui/controls
           (some (fn [c]
                   (let [{::control/keys [id]} c]
                     (when (= id id-key) c))))
           ::control/value))

(def refresh-control
  "config to add a refresh button to a report"
  {:type   :button
   :label  "Refresh"
   :action (fn [this] (control/run! this))})

(defn report-action
  [id-key mutation]
  (fn [this]
    (let [id     (get-control-value this id-key)]
      (comp/transact! this [(mutation {id-key id})]))))

(defn fetch-button
  [id-key mutation]
  {:type   :button
   :label  "Fetch"
   :action (report-action id-key mutation)})

(defn merge-state
  "Used by a show page's pre-merge to merge the parent id into the state"
  [state-map sub-page data]
  (merge
   (comp/get-initial-state sub-page)
   (get-in state-map (comp/get-ident sub-page {}))
   data))

(defn merge-pages
  [{:keys [data-tree state-map]} id-key page-map]
  (let [id (get data-tree id-key)]
    (log/finer :merge-pages/starting {:id-key id-key :id id :page-map page-map})
    (let [states (->> page-map
                      (map
                       (fn [[key page]]
                         (log/debug :merge-pages/process-page {:key key :page page})
                         (let [state (merge-state state-map page {id-key id})]
                           {key state})))
                      (into {}))]
      (merge data-tree states {:ui/page-merged true}))))

(defn row-action-button
  [label id-key mutation]
  {:label  label
   :action (fn [report-instance p]
             (let [id    (get p id-key)
                   props {id-key id}]
               (comp/transact! report-instance [(mutation props)])))})

(defn subrow-action-button
  [label id-key parent-key mutation]
  {:label  label
   :action (fn [report-instance p]
             (let [id    (get p id-key)
                   parent-id (get-control-value report-instance parent-key)
                   props {id-key id parent-key parent-id}]
               (comp/transact! report-instance [(mutation props)])))})

(defn sub-page-action-button
  [options]
  (let [{:keys [label mutation parent-key]} options]
    {:type  :button
     :label label
     :action
     (fn [report-instance]
       (let [parent-id (get-control-value report-instance parent-key)
             props     {parent-key parent-id}]
         (comp/transact! report-instance [(mutation props)])))}))

(def skip-loaded true)

(defn page-loader
  "Returns a will-enter handler for a page"
  [key control-key app {id :id}]
  (let [id             (new-uuid id)
        ident          [key id]
        parent-control (comp/registry-key->class control-key)
        state          (-> (app/current-state app) (get-in ident))]
    (log/info :page-loader/starting {:key key :control-key control-key :id id :state state})
    (if (and skip-loaded (:ui/page-merged state))
      (do
        (log/info :page-loader/routing-immediate {:ident ident})
        (dr/route-immediate ident))
      (do
        (log/info :page-loader/deferring {:ident ident})
        (dr/route-deferred
         ident
         (fn []
           (log/info :page-loader/routing {:key key :id id :parent-control parent-control})
           (df/load!
            app ident parent-control
            {:marker               :ui/selected-node
             :target               [:ui/selected-node]
             :post-mutation        `dr/target-ready
             :post-mutation-params {:target ident}})))))))

(defn subpage-loader
  [ident-key router-key Report this]
  (let [props    (comp/props this)
        chain-id (get-in props [[::dr/id router-key] ident-key])]
    (report/start-report! this Report {:route-params {ident-key chain-id}})))

(defn page-merger
  [k mappings]
  (log/info :page-merger/starting {:k k :mappings mappings})
  (fn [ctx]
    (log/info :page-merger/merging {:k k :mappings mappings :ctx ctx})
    (merge-pages ctx k mappings)))

(def blacklisted-keys
  #{:com.fulcrologic.fulcro.ui-state-machines/asm-id
    :com.fulcrologic.fulcro.application/active-remotes
    :com.fulcrologic.fulcro.ui-state-machines/ident->actor
    :com.fulcrologic.fulcro.ui-state-machines/state-machine-id
    :com.fulcrologic.fulcro.ui-state-machines/active-timers
    :com.fulcrologic.fulcro.ui-state-machines/actor->component-name
    :com.fulcrologic.fulcro.ui-state-machines/actor->ident})

(defn log-props
  [props]
  (dom/dl :.ui.segment
    (->> (keys props)
         (filter (fn [k] (not (blacklisted-keys k))))
         (map
          (fn [k]
            ^{:key k}
            (comp/fragment
             (dom/dt {} (str k))
             (dom/dd {}
               (let [v (get props k)]
                 (if (map? v)
                   (log-props v)
                   (do
                     (str v)
                     (if (vector? v)
                       (dom/ul {}
                         (map
                          (fn [vi]
                            ^{:key (str k vi)}
                            (dom/li {} (str vi)))
                          v))
                       (dom/div :.ui.segment (str v)))))))))))))

(declare ui-inner-prop-logger)

(defsc PropLineLogger
  [_this {:keys [key value]}]
  (comp/fragment
   (dom/dt {} (str key))
   (dom/dd {}
     (if (map? value)
       (ui-inner-prop-logger value)
       (if (vector? value)
         (str value)
         (str value))))))

(def ui-prop-line-logger (comp/factory PropLineLogger {:keyfn (comp str :key)}))

(defsc InnerPropLogger
  [_this props]
  (let [filtered-keys (filter (complement blacklisted-keys) (keys props))]
    (dom/dl {:style {:margin 0
                     :border "1px black solid"}}
      (map (fn [k] (ui-prop-line-logger {:key k :value (get props k)}))
           filtered-keys))))

(def ui-inner-prop-logger (comp/factory InnerPropLogger))

(defsc PropsLogger
  [_this props]
  (dom/div :.ui.segment
    (ui-inner-prop-logger props)))

(def ui-props-logger (comp/factory PropsLogger))

(defsc NavMenu
  [this {:keys [menu-items id]}]
  (ui-menu
   {:items menu-items
    :onItemClick
    (fn [_e d]
      (let [route-name (get (js->clj d) "route")
            route-kw   (keyword route-name)
            route      (comp/registry-key->class route-kw)]
        (log/info :onItemClick/kw {:route-kw route-kw :route route :id id})
        (if id
          (rroute/route-to! this route {:id (str id)})
          (log/info :onItemClick/no-id {}))))}))

(def ui-nav-menu (comp/factory NavMenu))

(form/defsc-form AccountLinkForm
  [this {::m.accounts/keys [id name]}]
  {fo/id         m.accounts/id
   fo/route-prefix "account-link"
   fo/attributes [m.accounts/name]}
  (form-link this id name :dinsro.ui.accounts/Show))

(def ui-account-link (comp/factory AccountLinkForm {:keyfn ::m.accounts/id}))

(form/defsc-form BlockLinkForm
  [this {::m.c.blocks/keys [id hash]}]
  {fo/id         m.c.blocks/id
   fo/route-prefix "block-link"
   fo/attributes [m.c.blocks/hash]}
  (log/info :BlockLinkForm/starting {:id id :hash hash})
  (form-link this id hash :dinsro.ui.core.blocks/ShowBlock))

(def ui-block-link (comp/factory BlockLinkForm {:keyfn ::m.c.blocks/id}))

(form/defsc-form BlockHeightLinkForm
  [this {::m.c.blocks/keys [id height]}]
  {fo/id         m.c.blocks/id
   fo/route-prefix "block-height-link"
   fo/attributes [m.c.blocks/height]}
  (form-link this id height :dinsro.ui.core.blocks/ShowBlock))

(def ui-block-height-link (comp/factory BlockHeightLinkForm {:keyfn ::m.c.blocks/id}))

(form/defsc-form CategoryLinkForm
  [this {::m.categories/keys [id name]}]
  {fo/id         m.categories/id
   fo/route-prefix "category-link"
   fo/attributes [m.categories/name]}
  (form-link this id name :dinsro.ui.categories/CategoryForm))

(def ui-category-link (comp/factory CategoryLinkForm {:keyfn ::m.categories/id}))

(form/defsc-form ChainLinkForm [this {::m.c.chains/keys [id name]}]
  {fo/id         m.c.chains/id
   fo/route-prefix "chain-link"
   fo/attributes [m.c.chains/name]}
  (form-link this id name :dinsro.ui.core.chains/ShowChain))

(def ui-chain-link (comp/factory ChainLinkForm {:keyfn ::m.c.chains/id}))

(form/defsc-form ChannelLinkForm [this {::m.ln.channels/keys [id channel-point]}]
  {fo/id         m.ln.channels/id
   fo/route-prefix "channel-link"
   fo/attributes [m.ln.channels/channel-point]}
  (form-link this id channel-point :dinsro.ui.ln.channels/LNChannelForm))

(def ui-channel-link (comp/factory ChannelLinkForm {:keyfn ::m.ln.channels/id}))

(form/defsc-form CoreNodeLinkForm
  [this {::m.c.nodes/keys [id name] :as props}]
  {fo/id         m.c.nodes/id
   fo/route-prefix "core-node-link"
   fo/attributes [m.c.nodes/id m.c.nodes/name]}
  (log/info :CoreNodeLinkForm/starting {:id id :name name :props props})
  (form-link this id (or name (str id)) :dinsro.ui.core.nodes/Show))

(def ui-core-node-link (comp/factory CoreNodeLinkForm {:keyfn ::m.c.nodes/id}))

(form/defsc-form CorePeerLinkForm
  [this {::m.c.peers/keys [id addr]}]
  {fo/id         m.c.peers/id
   fo/route-prefix "core-peer-link"
   fo/attributes [m.c.peers/id m.c.peers/addr]}
  (form-link this id addr :dinsro.ui.core.peers/ShowPeer))

(def ui-core-peer-link (comp/factory CorePeerLinkForm {:keyfn ::m.c.peers/id}))

(form/defsc-form CoreTxLinkForm
  [this {::m.c.tx/keys [id tx-id]}]
  {fo/id         m.c.tx/id
   fo/route-prefix "tx-link"
   fo/attributes [m.c.tx/id m.c.tx/tx-id]}
  (form-link this id tx-id :dinsro.ui.core.tx/ShowTransaction))

(def ui-core-tx-link (comp/factory CoreTxLinkForm {:keyfn ::m.c.tx/id}))

(form/defsc-form CurrencyLinkForm [this {::m.currencies/keys [id name]}]
  {fo/id         m.currencies/id
   fo/route-prefix "currency-link"
   fo/attributes [m.currencies/name]}
  (form-link this id name :dinsro.ui.currencies/ShowCurrency))

(def ui-currency-link (comp/factory CurrencyLinkForm {:keyfn ::m.currencies/name}))

(form/defsc-form DebitLinkForm [this {::m.debits/keys [id value]}]
  {fo/id         m.currencies/id
   fo/route-prefix "debit-link"
   fo/attributes [m.debits/value]}
  (form-link this id value :dinsro.ui.debits/ShowDebit))

(def ui-debit-link (comp/factory DebitLinkForm {:keyfn ::m.debits/name}))

(form/defsc-form InvoiceLinkForm [this {::m.ln.invoices/keys [id r-preimage]}]
  {fo/id         m.ln.invoices/id
   fo/route-prefix "invoice-link"
   fo/attributes [m.ln.invoices/r-preimage]}
  (form-link this id r-preimage :dinsro.ui.ln.invoices/LNInvoiceForm))

(def ui-invoice-link (comp/factory InvoiceLinkForm {:keyfn ::m.ln.invoices/id}))

(form/defsc-form LNPeerLinkForm [this {::m.ln.peers/keys [id remote-node]}]
  {fo/id         m.ln.peers/id
   fo/route-prefix "ln-peer-link"
   fo/attributes [m.ln.peers/remote-node]}

  (form-link this id remote-node :dinsro.ui.ln.peers/LNPeerForm))

(def ui-ln-peer-link (comp/factory LNPeerLinkForm {:keyfn ::m.ln.peers/id}))

(form/defsc-form NetworkLinkForm
  [this {::m.c.networks/keys [id name] :as props}]
  {fo/id         m.c.networks/id
   fo/route-prefix "network-link"
   fo/attributes [m.c.networks/name]}
  (log/finer :NetworkLinkForm/starting {:id id :name name :props props})
  (form-link this id name :dinsro.ui.core.networks/ShowNetwork))

(def ui-network-link (comp/factory NetworkLinkForm {:keyfn ::m.c.networks/id}))

(form/defsc-form NodeLinkForm
  [this {::m.ln.nodes/keys [id name] :as props}]
  {fo/id         m.ln.nodes/id
   fo/route-prefix "ln-node-link"
   fo/attributes [m.ln.nodes/name]}
  (log/finer :NodeLinkForm/starting {:id id :name name :props props})
  (form-link this id name :dinsro.ui.ln.nodes/Show))

(def ui-node-link (comp/factory NodeLinkForm {:keyfn ::m.ln.nodes/id}))

(form/defsc-form RemoteNodeLinkForm [this {::m.ln.remote-nodes/keys [id pubkey]}]
  {fo/id         m.ln.remote-nodes/id
   fo/route-prefix "remote-node-link"
   fo/attributes [m.ln.remote-nodes/pubkey]}
  (log/finer :RemoteNodeLinkForm/starting {:id id :pubkey pubkey})
  (form-link this id pubkey :dinsro.ui.ln.remote-nodes/ShowRemoteNode))

(def ui-remote-node-link (comp/factory RemoteNodeLinkForm {:keyfn ::m.ln.remote-nodes/id}))

(form/defsc-form PaymentsLinkForm [this {::m.ln.payments/keys [id payment-hash]}]
  {fo/id         m.ln.payments/id
   fo/route-prefix "payment-link"
   fo/attributes [m.ln.payments/payment-hash]}
  (form-link this id payment-hash :dinsro.ui.ln.payments/LNPaymentForm))

(def ui-payment-link (comp/factory PaymentsLinkForm {:keyfn ::m.ln.payments/id}))

(form/defsc-form PayReqLinkForm [this {::m.ln.payreqs/keys [id description]}]
  {fo/id         m.ln.payreqs/id
   fo/route-prefix "payreq-link"
   fo/attributes [m.ln.payreqs/description]}
  (form-link this id description :dinsro.ui.ln.payreqs/LNPaymentForm))

(def ui-payreq-link (comp/factory PayReqLinkForm {:keyfn ::m.ln.payreqs/id}))

(form/defsc-form PubkeyLinkForm [this {::m.n.pubkeys/keys [id hex]}]
  {fo/id           m.n.pubkeys/id
   fo/route-prefix "pubkey-link"
   fo/attributes   [m.n.pubkeys/hex]}
  (form-link this id hex :dinsro.ui.nostr.pubkeys/Show))

(def ui-pubkey-link (comp/factory PubkeyLinkForm {:keyfn ::m.n.pubkeys/id}))

(form/defsc-form RateLinkForm [this {::m.rates/keys [id date]}]
  {fo/id         m.rates/id
   fo/route-prefix "rate-link"
   fo/attributes [m.rates/date]}
  (form-link this id (str date) :dinsro.ui.rates/RateForm))

(def ui-rate-link (comp/factory RateLinkForm {:keyfn ::m.rates/id}))

(form/defsc-form RateSourceLinkForm [this {::m.rate-sources/keys [id name]}]
  {fo/id         m.rate-sources/id
   fo/route-prefix "rate-source-link"
   fo/attributes [m.rate-sources/name]}
  (form-link this id name :dinsro.ui.rate-sources/ShowRateSource))

(def ui-rate-source-link (comp/factory RateSourceLinkForm {:keyfn ::m.rate-sources/id}))

(form/defsc-form RelayLinkForm [this {::m.n.relays/keys [id address]}]
  {fo/id           m.n.relays/id
   fo/route-prefix "relay-link"
   fo/attributes   [m.n.relays/address]}
  (form-link this id address :dinsro.ui.nostr.relays/Show))

(def ui-relay-link (comp/factory RelayLinkForm {:keyfn ::m.n.relays/id}))

(form/defsc-form SubscriptionLinkForm [this {::m.n.subscriptions/keys [id code]}]
  {fo/id           m.n.subscriptions/id
   fo/route-prefix "subscription-link"
   fo/attributes   [m.n.subscriptions/code]}
  (form-link this id code :dinsro.ui.nostr.subscriptions/Show))

(def ui-subscription-link (comp/factory SubscriptionLinkForm {:keyfn ::m.n.subscriptions/id}))

(form/defsc-form SubscriptionPubkeyLinkForm [this {::m.n.subscription-pubkeys/keys [id]}]
  {fo/id           m.n.subscription-pubkeys/id
   fo/route-prefix "subscription-pubkey-link"
   fo/attributes   [m.n.subscription-pubkeys/id]}
  (form-link this id (str id) :dinsro.ui.nostr.subscription-pubkeys/Show))

(def ui-subscription-pubkey-link
  (comp/factory SubscriptionPubkeyLinkForm {:keyfn ::m.n.subscription-pubkeys/id}))

(form/defsc-form TransactionLinkForm [this {::m.transactions/keys [id description]}]
  {fo/id         m.transactions/id
   fo/route-prefix "transaction-link"
   fo/attributes [m.transactions/id m.transactions/description]}
  (form-link this id description :dinsro.ui.transactions/ShowTransaction))

(def ui-transaction-link (comp/factory TransactionLinkForm {:keyfn ::m.transactions/id}))

(form/defsc-form UserLinkForm [this {::m.users/keys [id name]}]
  {fo/id         m.users/id
   fo/route-prefix "user-link"
   fo/attributes [m.users/name]}
  (form-link this id name :dinsro.ui.users/ShowUser))

(def ui-user-link (comp/factory UserLinkForm {:keyfn ::m.users/id}))

(form/defsc-form WalletAddressLinkForm [this {::m.c.wallet-addresses/keys [id address]}]
  {fo/id         m.c.wallet-addresses/id
   fo/route-prefix "wallet-address-link"
   fo/attributes [m.c.wallet-addresses/address]}
  (form-link this id address :dinsro.ui.core.wallet-addresses/WalletAddressForm))

(def ui-wallet-address-link (comp/factory WalletAddressLinkForm {:keyfn ::m.c.wallet-addresses/id}))

(form/defsc-form WalletLinkForm [this {::m.c.wallets/keys [id name]}]
  {fo/id         m.c.wallets/id
   fo/route-prefix "wallet-link"
   fo/attributes [m.c.wallets/name]}
  (form-link this id name :dinsro.ui.core.wallets/ShowWallet))

(def ui-wallet-link (comp/factory WalletLinkForm {:keyfn ::m.c.wallets/id}))

(form/defsc-form WordLinkForm [this {::m.c.words/keys [id word]}]
  {fo/id         m.c.words/id
   fo/route-prefix "word-link"
   fo/attributes [m.c.words/word]}
  (form-link this id word :dinsro.ui.core.words/WordForm))

(def ui-word-link (comp/factory WordLinkForm {:keyfn ::m.c.words/id}))

(defn report-link
  [key-fn link-fn]
  (fn [this value]
    (let [{:ui/keys [current-rows]} (comp/props this)]
      (if-let [row (first (filter #(= (key-fn %) value) current-rows))]
        (link-fn row)
        (dom/p {} "not found")))))
