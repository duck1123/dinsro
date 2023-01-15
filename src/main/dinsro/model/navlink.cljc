(ns dinsro.model.navlink
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [com.wsscode.pathom.connect :as pc]))

(comment ::pc/_)

(def links
  {:accounts              ["Accounts"          "/accounts"         :dinsro.ui.accounts/Report]
   :admin                 ["Admin"             "/admin/users"      :dinsro.ui.admin/AdminPage]
   :categories            ["Categories"        "/categories"       :dinsro.ui.categories/Report]
   :channels              ["Channels"          "/ln/channels"      :dinsro.ui.ln.channels/Report]
   :core-addresses        ["Core Address"      "/core/addresses"   :dinsro.ui.core.addresses/Report]
   :core-blocks           ["Core Blocks"       "/core/blocks"      :dinsro.ui.core.blocks/Report]
   :core-chains           ["Chains"            "/core/chains"      :dinsro.ui.core.chains/Report]
   :core-menu             ["Core"              "/core"             nil
                           [:core-nodes
                            :core-peers
                            :core-blocks
                            :core-transactions
                            :core-addresses
                            :core-chains
                            :core-networks
                            :wallets
                            :words]]
   :core-node-connections ["Connections"       "/core/connections"  :dinsro.ui.core.connections/Report]
   :core-networks         ["Networks"          "/core/networks"     :dinsro.ui.core.networks/Report]
   :core-nodes            ["Nodes"             "/core/nodes"        :dinsro.ui.core.nodes/Report]
   :core-peers            ["Peers"             "/core/peers"        :dinsro.ui.core.peers/Report]
   :core-transactions     ["Transactions"      "/core/transactions" :dinsro.ui.core.tx/Report]
   :currencies            ["Currencies"        "/currencies"        :dinsro.ui.currencies/Report]
   :home                  ["Home"              "/"                  :dinsro.ui.home/HomePage]
   :invoices              ["Invoices"          "/ln/invoices"       :dinsro.ui.ln.invoices/Report]
   :lightning-nodes       ["Lightning Nodes"   "/ln/nodes"          :dinsro.ui.ln.nodes/Report]
   :ln-menu               ["LN"                "/ln"                nil
                           [:lightning-nodes
                            :ln-remote-nodes
                            :peers
                            :channels
                            :invoices
                            :payreqs
                            :payments]]
   :ln-remote-nodes       ["Remote Nodes"      "/ln/remote-nodes"  :dinsro.ui.ln.remote-nodes/Report]
   :login                 ["Login"             "/login"            :dinsro.ui.login/LoginPage]
   :nostr                 ["Nostr"             "/nostr"            nil
                           [:nostr-relays :nostr-pubkeys :nostr-events]]
   :nostr-pubkeys         ["Pubkeys"           "/pubkeys"          :dinsro.ui.nostr.pubkeys/Report]
   :nostr-events          ["Events"            "/events"           :dinsro.ui.nostr.events/Report]
   :nostr-relays          ["Relays"            "/relays"           :dinsro.ui.nostr.relays/Report]
   :payments              ["Payments"          "/payments"         :dinsro.ui.ln.payments/Report]
   :payreqs               ["Payreqs"           "/payreqs"          :dinsro.ui.ln.payreqs/Report]
   :peers                 ["Peers"             "/ln/peers"         :dinsro.ui.ln.peers/Report]
   :rates                 ["Rates"             "/rates"            :dinsro.ui.rates/Report]
   :rate-sources          ["Rate Sources"      "/rate-sources"     :dinsro.ui.rate-sources/Report]
   :registration          ["Registration"      "/register"         :dinsro.ui.registration/RegistrationPage]
   :settings              ["Settings"          "/settings"         :dinsro.ui.settings/SettingsPage]
   :transactions          ["Transactions"      "/transactions"     :dinsro.ui.transactions/Report]
   :users                 ["Users"             "/users"            :dinsro.ui.users/Report]
   :wallets               ["Wallets"           "/wallets"          :dinsro.ui.core.wallets/Report]
   :wallet-addresses      ["Wallet Addresses"  "/wallet-addresses" :dinsro.ui.core.wallet-addresses/Report]
   :words                 ["Words"             "/core/words"       :dinsro.ui.core.words/Report]})

#?(:clj
   (pc/defresolver id
     [_env _props]
     {ao/pc-output  [::id]
      ao/pc-resolve (fn [_env _props] {})}))

(s/def ::name string?)
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
