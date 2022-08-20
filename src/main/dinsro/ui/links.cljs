(ns dinsro.ui.links
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.model.ln.transactions :as m.ln.tx]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.words :as m.c.words]
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
  [this id-key]
  (some->> this comp/props
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

(form/defsc-form AccountLinkForm
  [this {::m.accounts/keys [id name]}]
  {fo/id           m.accounts/id
   fo/route-prefix "account-link"
   fo/title        "Accounts"
   fo/attributes   [m.accounts/name]}
  (form-link this id name :dinsro.ui.accounts/AccountForm))

(def ui-account-link (comp/factory AccountLinkForm {:keyfn ::m.accounts/id}))

(form/defsc-form BlockLinkForm
  [this {::m.c.blocks/keys [id hash]}]
  {fo/id           m.c.blocks/id
   fo/route-prefix "block-link"
   fo/title        "Blocks"
   fo/attributes   [m.c.blocks/hash]}
  (log/info :BlockLinkForm/starting {:id id :hash hash})
  (form-link this id hash :dinsro.ui.core.blocks/ShowBlock))

(def ui-block-link (comp/factory BlockLinkForm {:keyfn ::m.c.blocks/id}))

(form/defsc-form BlockHeightLinkForm
  [this {::m.c.blocks/keys [id height]}]
  {fo/id           m.c.blocks/id
   fo/route-prefix "block-height-link"
   fo/title        "Blocks"
   fo/attributes   [m.c.blocks/height]}
  (form-link this id height :dinsro.ui.core.blocks/ShowBlock))

(def ui-block-height-link (comp/factory BlockHeightLinkForm {:keyfn ::m.c.blocks/id}))

(form/defsc-form CategoryLinkForm
  [this {::m.categories/keys [id name]}]
  {fo/id           m.categories/id
   fo/route-prefix "category-link"
   fo/attributes   [m.categories/name]}
  (form-link this id name :dinsro.ui.categories/CategoryForm))

(def ui-category-link (comp/factory CategoryLinkForm {:keyfn ::m.categories/id}))

(form/defsc-form ChainLinkForm [this {::m.c.chains/keys [id name]}]
  {fo/id           m.c.chains/id
   fo/route-prefix "chain-link"
   fo/title        "Chains"
   fo/attributes   [m.c.chains/name]}
  (form-link this id name :dinsro.ui.core.chains/ShowChain))

(def ui-chain-link (comp/factory ChainLinkForm {:keyfn ::m.c.chains/id}))

(form/defsc-form ChannelLinkForm [this {::m.ln.channels/keys [id channel-point]}]
  {fo/id           m.ln.channels/id
   fo/route-prefix "channel-link"
   fo/title        "Channels"
   fo/attributes   [m.ln.channels/channel-point]}
  (form-link this id channel-point :dinsro.ui.ln.channels/LNChannelForm))

(def ui-channel-link (comp/factory ChannelLinkForm {:keyfn ::m.ln.channels/id}))

(form/defsc-form CoreNodeLinkForm
  [this {::m.c.nodes/keys [id name] :as props}]
  {fo/id           m.c.nodes/id
   fo/route-prefix "core-node-link"
   fo/attributes   [m.c.nodes/id m.c.nodes/name]}
  (log/info :CoreNodeLinkForm/starting {:id id :name name :props props})
  (form-link this id (or name (str id)) :dinsro.ui.core.nodes/ShowNode))

(def ui-core-node-link (comp/factory CoreNodeLinkForm {:keyfn ::m.c.nodes/id}))

(form/defsc-form CorePeerLinkForm
  [this {::m.c.peers/keys [id addr]}]
  {fo/id           m.c.peers/id
   fo/route-prefix "core-peer-link"
   fo/title        "Peers"
   fo/attributes   [m.c.peers/id m.c.peers/addr]}
  (form-link this id addr :dinsro.ui.core.peers/CorePeerForm))

(def ui-core-peer-link (comp/factory CorePeerLinkForm {:keyfn ::m.c.peers/id}))

(form/defsc-form CoreTxLinkForm
  [this {::m.c.tx/keys [id tx-id]}]
  {fo/id           m.c.tx/id
   fo/route-prefix "core-tx-link"
   fo/title        "Transaction"
   fo/attributes   [m.c.tx/id m.c.tx/tx-id]}
  (form-link this id tx-id :dinsro.ui.core.tx/ShowTransaction))

(def ui-core-tx-link (comp/factory CoreTxLinkForm {:keyfn ::m.c.tx/id}))

(form/defsc-form CurrencyLinkForm [this {::m.currencies/keys [id name]}]
  {fo/id           m.currencies/id
   fo/route-prefix "currency-link"
   fo/title        "Currency"
   fo/attributes   [m.currencies/name]}
  (form-link this id name :dinsro.ui.currencies/CurrencyForm))

(def ui-currency-link (comp/factory CurrencyLinkForm {:keyfn ::m.currencies/name}))

(form/defsc-form InvoiceLinkForm [this {::m.ln.invoices/keys [id r-preimage]}]
  {fo/id           m.ln.invoices/id
   fo/route-prefix "node-link"
   fo/attributes   [m.ln.invoices/r-preimage]}
  (form-link this id r-preimage :dinsro.ui.ln.invoices/LNInvoiceForm))

(def ui-invoice-link (comp/factory InvoiceLinkForm {:keyfn ::m.ln.invoices/id}))

(form/defsc-form LNPeerLinkForm [this {::m.ln.peers/keys [id pubkey]}]
  {fo/id           m.ln.peers/id
   fo/route-prefix "ln-peer-link"
   fo/title        "Peers"
   fo/attributes   [m.ln.peers/pubkey]}
  (form-link this id pubkey :dinsro.ui.ln.peers/LNPeerForm))

(def ui-ln-peer-link (comp/factory LNPeerLinkForm {:keyfn ::m.ln.peers/id}))

(form/defsc-form LNTxLinkForm [this {::m.ln.tx/keys [id tx-hash]}]
  {fo/id           m.ln.tx/id
   fo/route-prefix "ln-tx-link"
   fo/attributes   [m.ln.tx/tx-hash]}
  (form-link this id tx-hash :dinsro.ui.ln.transactions/LNTransactionForm))

(def ui-ln-tx-link (comp/factory LNTxLinkForm {:keyfn ::m.ln.tx/id}))

(form/defsc-form NetworkLinkForm
  [this {::m.c.networks/keys [id name] :as props}]
  {fo/id           m.c.networks/id
   fo/route-prefix "network-link"
   fo/title        "Network"
   fo/attributes   [m.c.networks/name]}
  (log/finer :NetworkLinkForm/starting {:id id :name name :props props})
  (form-link this id name :dinsro.ui.core.networks/ShowNetwork))

(def ui-network-link (comp/factory NetworkLinkForm {:keyfn ::m.c.networks/id}))

(form/defsc-form NodeLinkForm
  [this {::m.ln.nodes/keys [id name] :as props}]
  {fo/id           m.ln.nodes/id
   fo/route-prefix "node-link"
   fo/title        "LN Node"
   fo/attributes   [m.ln.nodes/name]}
  (log/finer :NodeLinkForm/starting {:id id :name name :props props})
  (form-link this id name :dinsro.ui.ln.nodes/ShowNode))

(def ui-node-link (comp/factory NodeLinkForm {:keyfn ::m.ln.nodes/id}))

(form/defsc-form RemoteNodeLinkForm [this {::m.ln.remote-nodes/keys [id pubkey]}]
  {fo/id           m.ln.remote-nodes/id
   fo/route-prefix "remote-node-link"
   fo/title        "Remote Node"
   fo/attributes   [m.ln.remote-nodes/pubkey]}
  (log/finer :RemoteNodeLinkForm/starting {:id id :pubkey pubkey})
  (form-link this id pubkey :dinsro.ui.ln.remote-nodes/ShowRemoteNode))

(def ui-remote-node-link (comp/factory RemoteNodeLinkForm {:keyfn ::m.ln.remote-nodes/id}))

(form/defsc-form PaymentsLinkForm [this {::m.ln.payments/keys [id payment-hash]}]
  {fo/id           m.ln.payments/id
   fo/route-prefix "payment-link"
   fo/title        "Payments"
   fo/attributes   [m.ln.payments/payment-hash]}
  (form-link this id payment-hash :dinsro.ui.ln.payments/LNPaymentForm))

(def ui-payment-link (comp/factory PaymentsLinkForm {:keyfn ::m.ln.payments/id}))

(form/defsc-form PayReqLinkForm [this {::m.ln.payreqs/keys [id description]}]
  {fo/id           m.ln.payreqs/id
   fo/route-prefix "payreq-link"
   fo/title        "Payment Requests"
   fo/attributes   [m.ln.payreqs/description]}
  (form-link this id description :dinsro.ui.ln.payreqs/LNPaymentForm))

(def ui-payreq-link (comp/factory PayReqLinkForm {:keyfn ::m.ln.payreqs/id}))

(form/defsc-form RateLinkForm [this {::m.rates/keys [id date]}]
  {fo/id           m.rates/id
   fo/route-prefix "rate-link"
   fo/attributes   [m.rates/date]}
  (form-link this id (str date) :dinsro.ui.rates/RateForm))

(def ui-rate-link (comp/factory RateLinkForm {:keyfn ::m.rates/id}))

(form/defsc-form RateSourceLinkForm [this {::m.rate-sources/keys [id name]}]
  {fo/id           m.rate-sources/id
   fo/route-prefix "rate-source-link"
   fo/attributes   [m.rate-sources/name]}
  (form-link this id name :dinsro.ui.rate-sources/RateSourceForm))

(def ui-rate-source-link (comp/factory RateSourceLinkForm {:keyfn ::m.rate-sources/id}))

(form/defsc-form TransactionLinkForm [this {::m.transactions/keys [id description]}]
  {fo/id           m.transactions/id
   fo/route-prefix "transaction-link"
   fo/title        "Transactions"
   fo/attributes   [m.transactions/id m.transactions/description]}
  (form-link this id description :dinsro.ui.transactions/TransactionForm))

(def ui-transaction-link (comp/factory TransactionLinkForm {:keyfn ::m.transactions/id}))

(form/defsc-form UserLinkForm [this {::m.users/keys [id name]}]
  {fo/id           m.users/id
   fo/route-prefix "user-link"
   fo/attributes   [m.users/name]
   fo/title        "User"}
  (form-link this id name :dinsro.ui.users/ShowUser))

(def ui-user-link (comp/factory UserLinkForm {:keyfn ::m.users/id}))

(form/defsc-form WalletAddressLinkForm [this {::m.c.wallet-addresses/keys [id address]}]
  {fo/id           m.c.wallet-addresses/id
   fo/route-prefix "wallet-addresses-link"
   fo/attributes   [m.c.wallet-addresses/address]
   fo/title        "Wallet Addresses"}
  (form-link this id address :dinsro.ui.core.wallet-addresses/WalletAddressForm))

(def ui-wallet-address-link (comp/factory WalletAddressLinkForm {:keyfn ::m.c.wallet-addresses/id}))

(form/defsc-form WalletLinkForm [this {::m.c.wallets/keys [id name]}]
  {fo/id           m.c.wallets/id
   fo/route-prefix "wallets-link"
   fo/attributes   [m.c.wallets/name]
   fo/title        "Wallet"}
  (form-link this id name :dinsro.ui.core.wallets/ShowWallet))

(def ui-wallet-link (comp/factory WalletLinkForm {:keyfn ::m.c.wallets/id}))

(form/defsc-form WordLinkForm [this {::m.c.words/keys [id word]}]
  {fo/id           m.c.words/id
   fo/route-prefix "wordss-link"
   fo/attributes   [m.c.words/word]
   fo/title        "Word"}
  (form-link this id word :dinsro.ui.core.words/WordForm))

(def ui-word-link (comp/factory WordLinkForm {:keyfn ::m.c.words/id}))

(defn report-link
  [key-fn link-fn]
  (fn [this value]
    (let [{:ui/keys [current-rows]} (comp/props this)]
      (if-let [row (first (filter #(= (key-fn %) value) current-rows))]
        (link-fn row)
        (dom/p {} "not found")))))
