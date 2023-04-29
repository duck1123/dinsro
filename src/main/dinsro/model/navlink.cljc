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
  {:accounts                   ["Accounts"              :dinsro.ui.accounts/Report]
   :admin                      ["Admin"                 :dinsro.ui.admin/AdminPage]
   :categories                 ["Categories"            :dinsro.ui.categories/Report]
   :channels                   ["Channels"              :dinsro.ui.ln.channels/Report]
   :contacts                   ["Contacts"              :dinsro.ui.contacts/Report]
   :core-addresses             ["Core Address"          :dinsro.ui.core.addresses/Report]
   :core-blocks                ["Core Blocks"           :dinsro.ui.core.blocks/Report]
   :core-chains                ["Chains"                :dinsro.ui.core.chains/Report]
   :core-menu                  ["Core"                  nil
                                [:core-nodes :core-peers :core-blocks :core-transactions
                                 :core-addresses :core-chains :core-networks :wallets]]
   :core-node-connections      ["Connections"           :dinsro.ui.core.connections/Report]
   :core-networks              ["Networks"              :dinsro.ui.core.networks/Report]
   :core-nodes                 ["Nodes"                 :dinsro.ui.core.nodes/Report]
   :core-peers                 ["Peers"                 :dinsro.ui.core.peers/Report]
   :core-transactions          ["Transactions"          :dinsro.ui.core.transactions/Report]
   :currencies                 ["Currencies"            :dinsro.ui.currencies/Report]
   :home                       ["Home"                  :dinsro.ui.home/HomePage]
   :invoices                   ["Invoices"              :dinsro.ui.ln.invoices/Report]
   :lightning-nodes            ["Lightning Nodes"       :dinsro.ui.ln.nodes/Report]
   :ln-menu                    ["LN"                    nil
                                [:lightning-nodes :ln-remote-nodes :peers :channels :invoices :payreqs :payments]]
   :ln-remote-nodes            ["Remote Nodes"          :dinsro.ui.ln.remote-nodes/Report]
   :login                      ["Login"                 :dinsro.ui.login/LoginPage]
   :nodes                      ["Nodes"                 :dinsro.ui.nodes/Dashboard]
   :nostr                      ["Nostr"                 nil
                                [:nostr-relays :nostr-pubkeys :nostr-events
                                 :nostr-badge-definitions
                                 :nostr-badge-acceptance
                                 :nostr-badge-awards
                                 :nostr-connections]]
   :nostr-badge-acceptance     ["Badge Acceptance"      :dinsro.ui.nostr.badge-acceptance/Report]
   :nostr-badge-awards         ["Badge Awards"          :dinsro.ui.nostr.badge-awards/Report]
   :nostr-badge-definitions    ["Badge Definitions"     :dinsro.ui.nostr.badge-definitions/Report]
   :nostr-connections          ["Connections"           :dinsro.ui.nostr.connections/Report]
   :nostr-events               ["Events"                :dinsro.ui.nostr.events/Report]
   :nostr-pubkeys              ["Pubkeys"               :dinsro.ui.nostr.pubkeys/Report]
   :nostr-relays               ["Relays"                :dinsro.ui.nostr.relays/Report]
   :payments                   ["Payments"              :dinsro.ui.ln.payments/Report]
   :payreqs                    ["Payreqs"               :dinsro.ui.ln.payreqs/Report]
   :peers                      ["Peers"                 :dinsro.ui.ln.peers/Report]
   :registration               ["Registration"          :dinsro.ui.registration/RegistrationPage]
   :settings                   ["Settings"              :dinsro.ui.settings/SettingsPage]
   :transactions               ["Transactions"          :dinsro.ui.transactions/Report]
   :users                      ["Users"                 :dinsro.ui.admin.users/Report]
   :wallets                    ["Wallets"               :dinsro.ui.core.wallets/Report]
   :wallet-addresses           ["Wallet Addresses"      :dinsro.ui.core.wallet-addresses/Report]
   :words                      ["Words"                 :dinsro.ui.core.words/Report]})

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

(defattr target ::target :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::target]
   ao/pc-resolve (fn [_env {::keys [id]}] {::target (nth (get links id) 1)})})

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
                               ids  (or (when (> (count link) 2) (nth link 2)) [])]
                           {::children (idents ids)}))
   ::report/column-EQL {::children [::id ::name]}})

(defattr index ::index :ref
  {ao/target    ::id
   ao/pc-output [{::index [::id]}]
   ao/pc-resolve
   (fn [_env _props] {::index (idents (keys links))})})

(def attributes [children name target auth-link? index])

#?(:clj (def resolvers [id]))
