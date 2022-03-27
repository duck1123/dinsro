(ns dinsro.model.navlink
  (:refer-clojure :exclude [name])
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [com.wsscode.pathom.connect :as pc]))

(comment ::pc/_)

(def links
  {:accounts         ["Accounts"          "/accounts"         :dinsro.ui.accounts/AccountsReport]
   :admin            ["Admin"             "/admin/users"      :dinsro.ui.admin/AdminPage]
   :categories       ["Categories"        "/categories"       :dinsro.ui.categories/CategoriesReport]
   :channels         ["Channels"          "/ln-channels"      :dinsro.ui.ln-channels/LNChannelsReport]
   :core-addresses   ["Core Address"      "/core-addresses"   :dinsro.ui.core-addresses/CoreAddressReport]
   :core-blocks      ["Core Blocks"       "/core-blocks"      :dinsro.ui.core-blocks/CoreBlockReport]
   :core-menu        ["Core" "/core" nil
                      [:core-nodes
                       :core-peers
                       :core-blocks
                       :core-txes
                       :core-addresses
                       :wallets
                       :wallet-addresses]]
   :core-nodes       ["Core Nodes"        "/core-nodes"       :dinsro.ui.core-nodes/CoreNodesReport]
   :core-peers       ["Core Peers"        "/core-peers"       :dinsro.ui.core-peers/CorePeersReport]
   :core-txes        ["Core Transactions" "/core-txes"        :dinsro.ui.core-tx/CoreTxReport]
   :currencies       ["Currencies"        "/currencies"       :dinsro.ui.currencies/CurrenciesReport]
   :home             ["Home"              "/"                 :dinsro.ui.home/HomePage2]
   :invoices         ["Invoices"          "/ln-invoices"      :dinsro.ui.ln-invoices/LNInvoicesReport]
   :lightning-nodes  ["Lightning Nodes"   "/ln-nodes"         :dinsro.ui.ln-nodes/LightningNodesReport]
   :ln-menu          ["LN" "/ln" nil [:lightning-nodes
                                      ;; :ln-remote-nodes
                                      :peers
                                      :channels
                                      :invoices
                                      :tx
                                      :payreqs
                                      :payments]]
   :ln-remote-nodes  ["Remote Nodes"      "/ln-remote-nodes"  :dinsro.ui.ln-remote-nodes/RemoteNodesReport]
   :login            ["Login"             "/login"            :dinsro.ui.login/LoginPage]
   :payments         ["Payments"          "/payments"         :dinsro.ui.ln-payments/LNPaymentsReport]
   :payreqs          ["Payreqs"           "/payreqs"          :dinsro.ui.ln-payreqs/LNPayreqsReport]
   :peers            ["Peers"             "/ln-peers"         :dinsro.ui.ln-peers/LNPeersReport]
   :rates            ["Rates"             "/rates"            :dinsro.ui.rates/RatesReport]
   :rate-sources     ["Rate Sources"      "/rate-sources"     :dinsro.ui.rate-sources/RateSourcesReport]
   :registration     ["Registration"      "/register"         :dinsro.ui.registration/RegistrationPage]
   :settings         ["Settings"          "/settings"         :dinsro.ui.settings/SettingsPage]
   :transactions     ["Transactions"      "/transactions"     :dinsro.ui.transactions/TransactionsReport]
   :tx               ["LN TXes"           "/ln-transactions"  :dinsro.ui.ln-transactions/LNTransactionsReport]
   :users            ["Users"             "/users"            :dinsro.ui.users/UsersReport]
   :wallets          ["Wallets"           "/wallets"          :dinsro.ui.wallets/WalletReport]
   :wallet-addresses ["Wallet Addresses"  "/wallet-addresses" :dinsro.ui.wallet-addresses/WalletAddressesReport]})

#?(:clj
   (pc/defresolver id
     [_env _props]
     {ao/pc-output  [::id]
      ao/pc-resolve (fn [_env _props] {})}))

(defattr name ::name :string
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::name]
   ao/pc-resolve (fn [_env {::keys [id]}] {::name (nth (get links id) 0)})})

(defattr href ::href :string
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::href]
   ao/pc-resolve (fn [_env {::keys [id]}] {::href (nth (get links id) 1)})})

(defattr target ::target :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::target]
   ao/pc-resolve (fn [_env {::keys [id]}] {::target (nth (get links id) 2)})})

(defattr auth-link? ::auth-link? :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::auth-link?]
   ao/pc-resolve (fn [_env {::keys [id]}] {::auth-link? (not (nil? (#{:login} id)))})})

(defn idents
  [ids]
  (map (fn [id] {::id id}) ids))

(defattr children ::children :ref
  {ao/identities       #{::id}
   ao/pc-input         #{::id}
   ao/target           ::id
   ao/pc-output        [::children]
   ao/pc-resolve       (fn [_env {::keys [id]}]
                         (let [link (get links id)
                               ids  (or (when (> (count link) 3) (nth link 3)) [])]
                           {::children (idents ids)}))
   ::report/column-EQL {::children [::id ::name]}})

(defattr index ::index :ref
  {ao/target    ::id
   ao/pc-output [{::index [::id]}]
   ao/pc-resolve
   (fn [_env _props] {::index (idents (keys links))})})

(def attributes
  [children
   name
   href
   target
   auth-link?
   index])

#?(:clj (def resolvers [id]))
