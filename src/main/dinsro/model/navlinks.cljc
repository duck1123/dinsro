(ns dinsro.model.navlinks
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.wsscode.pathom.connect :as pc]))

#?(:cljs (comment ::pc/_))

(def routes
  {:accounts
   {::label "Accounts"
    ::route :dinsro.ui.accounts/Report}

   :admin
   {::label "Admin"
    ::route :dinsro.ui.admin.users/Report}

   :admin-accounts
   {::label "Accounts"
    ::route :dinsro.ui.admin.accounts/Report}

   :admin-categories
   {::label "Categories"
    ::route :dinsro.ui.admin.categories/Report}

   :admin-core
   {::label "Core"
    ::route :dinsro.ui.admin.core.dashboard/Dashboard}

   :admin-core-addresses
   {::label "Addresses"
    ::route :dinsro.ui.admin.core.addresses/Report}

   :admin-core-blocks
   {::label "Blocks"
    ::route :dinsro.ui.admin.core.blocks/Report}

   :admin-core-chains
   {::label "Chains"
    ::route :dinsro.ui.admin.core.chains/Report}

   :admin-core-dashboard
   {::label "Dashboard"
    ::route :dinsro.ui.admin.core.dashboard/Dashboard}

   :admin-core-mnemonics
   {::label "Mnemonics"
    ::route :dinsro.ui.admin.core.mnemonics/Report}

   :admin-core-networks
   {::label "Networks"
    ::route :dinsro.ui.admin.core.networks/Report}

   :admin-core-nodes
   {::label "Nodes"
    ::route :dinsro.ui.admin.core.nodes/Report}

   :admin-core-peers
   {::label "Peers"
    ::route :dinsro.ui.admin.core.peers/Report}

   :admin-core-transactions
   {::label "Transactions"
    ::route :dinsro.ui.admin.core.transactions/Report}

   :admin-core-wallets
   {::label "Wallets"
    ::route :dinsro.ui.admin.core.wallets/Report}

   :admin-core-words
   {::label "Words"
    ::route :dinsro.ui.admin.core.words/Report}

   :admin-currencies
   {::label "Currencies"
    ::route :dinsro.ui.admin.currencies/Report}

   :admin-debits
   {::label "Debits"
    ::route :dinsro.ui.admin.debits/Report}

   :admin-ln
   {::label "LN"
    ::route :dinsro.ui.admin.ln/Dashboard}

   :admin-ln-accounts
   {::label "Accounts"
    ::route :dinsro.ui.admin.ln.accounts/Report}

   :admin-ln-channels
   {::label "Channels"
    ::route :dinsro.ui.admin.ln.channels/Report}

   :admin-ln-dashboard
   {::label "Dashboard"
    ::route :dinsro.ui.admin.ln/Dashboard}

   :admin-ln-invoices
   {::label "Invoices"
    ::route :dinsro.ui.admin.ln.invoices/Report}

   :admin-ln-nodes
   {::label "Nodes"
    ::route :dinsro.ui.admin.ln.nodes/Report}

   :admin-ln-payments
   {::label "Payments"
    ::route :dinsro.ui.admin.ln.payments/Report}

   :admin-ln-payreqs
   {::label "Payment Requests"
    ::route :dinsro.ui.admin.ln.payreqs/Report}

   :admin-ln-peers
   {::label "Peers"
    ::route :dinsro.ui.admin.ln.peers/Report}

   :admin-ln-remote-nodes
   {::label "Remote Nodes"
    ::route :dinsro.ui.admin.ln.remote-nodes/Report}

   :admin-nostr
   {::label "Nostr"
    ::route :dinsro.ui.admin.nostr.dashboard/Dashboard}

   :admin-nostr-badge-acceptances
   {::label "Acceptances"
    ::route :dinsro.ui.admin.nostr.badge-acceptances/Report}

   :admin-nostr-badge-awards
   {::label "Awards"
    ::route :dinsro.ui.admin.nostr.badge-awards/Report}

   :admin-nostr-badge-definitions
   {::label "Definitions"
    ::route :dinsro.ui.admin.nostr.badge-definitions/Report}

   :admin-nostr-connections
   {::label "Connections"
    ::route :dinsro.ui.admin.nostr.connections/Report}

   :admin-nostr-connections-runs
   {::label "Runs"
    ::route :dinsro.ui.nostr.connections.runs/SubPage}

   :admin-nostr-dashboard
   {::label "dashboard"
    ::route :dinsro.ui.admin.nostr.dashboard/Dashboard}

   :admin-nostr-events
   {::label "Events"
    ::route :dinsro.ui.admin.nostr.events/Report}

   :admin-nostr-filter-items
   {::label "Items"
    ::route :dinsro.ui.admin.nostr.filter-items/Report}

   :admin-nostr-filters
   {::label "Filters"
    ::route :dinsro.ui.admin.nostr.filters/Report}

   :admin-nostr-pubkeys
   {::label "Pubkeys"
    ::route :dinsro.ui.admin.nostr.pubkeys/Report}

   :admin-nostr-relays
   {::label "Relays"
    ::route :dinsro.ui.admin.nostr.relays/Report}

   :admin-nostr-relays-connections
   {::label "Connections"
    ::route :dinsro.ui.admin.nostr.relays.connections/SubPage}

   :admin-nostr-relays-events
   {::label "Events"
    ::route :dinsro.ui.admin.nostr.relays.events/SubPage}

   :admin-nostr-relays-pubkeys
   {::label "Pubkeys"
    ::route :dinsro.ui.admin.nostr.relays.pubkeys/SubPage}

   :admin-nostr-relays-requests
   {::label "Requests"
    ::route :dinsro.ui.admin.nostr.relays.requests/SubPage}

   :admin-nostr-relays-runs
   {::label "Runs"
    ::route :dinsro.ui.admin.nostr.relays.runs/SubPage}

   :admin-nostr-relays-witnesses
   {::label "Witnesses"
    ::route :dinsro.ui.admin.nostr.relays.witnesses/SubPage}

   :admin-nostr-requests
   {::label "Requests"
    ::route :dinsro.ui.admin.nostr.requests/Report}

   :admin-nostr-requests-connections
   {::label "Connections"
    ::route :dinsro.ui.admin.nostr.requests.connections/SubPage}

   :admin-nostr-requests-filters
   {::label "Filters"
    ::route :dinsro.ui.admin.nostr.requests.filters/SubPage}

   :admin-nostr-requests-items
   {::label "Items"
    ::route :dinsro.ui.admin.nostr.requests.filter-items/SubPage}

   :admin-nostr-requests-runs
   {::label "Runs"
    ::route :dinsro.ui.admin.nostr.requests.runs/SubPage}

   :admin-nostr-runs
   {::label "Runs"
    ::route :dinsro.ui.admin.nostr.runs/Report}

   :admin-nostr-witnesses
   {::label "Witnesses"
    ::route :dinsro.ui.admin.nostr.witnesses/Report}

   :admin-rate-sources
   {::label "Rate Sources"
    ::route :dinsro.ui.admin.rate-sources/Report}

   :admin-rates
   {::label "Rates"
    ::route :dinsro.ui.admin.rates/Report}

   :admin-transactions
   {::label "Transactions"
    ::route :dinsro.ui.admin.transactions/Report}

   :admin-users
   {::label "users"
    ::route :dinsro.ui.admin.users/Report}

   :admin-users-accounts
   {::label "Accounts"
    ::route :dinsro.ui.admin.users.accounts/SubPage}

   :admin-users-categories
   {::label "Categories"
    ::route :dinsro.ui.admin.users.categories/SubPage}

   :admin-users-debits
   {::label "Debits"
    ::route :dinsro.ui.admin.users.debits/SubPage}

   :admin-users-ln-nodes
   {::label "LN Nodes"
    ::route :dinsro.ui.admin.users.ln-nodes/SubPage}

   :admin-users-pubkeys
   {::label "Pubkeys"
    ::route :dinsro.ui.admin.users.pubkeys/SubPage}

   :admin-users-transactions
   {::label "Transactions"
    ::route :dinsro.ui.admin.users.transactions/SubPage}

   :admin-users-user-pubkeys
   {::label "User Pubkeys"
    ::route :dinsro.ui.admin.users.user-pubkeys/SubPage}

   :admin-users-wallets
   {::label "Wallets"
    ::route :dinsro.ui.admin.users.wallets/SubPage}

   :contacts
   {::label "Contacts"
    ::route :dinsro.ui.contacts/Report}

   :core-chains-networks
   {::label "Networks"
    ::route :dinsro.ui.core.chain-networks/SubPage}

   :core-networks-addresses
   {::label "Addresses"
    ::route :dinsro.ui.core.networks.addresses/SubPage}

   :core-networks-blocks
   {::label "Blocks"
    ::route :dinsro.ui.core.networks.blocks/SubPage}

   :core-networks-core-nodes
   {::label "Core Nodes"
    ::route :dinsro.ui.core.networks.nodes/SubPage}

   :core-networks-ln-nodes
   {::label "LN Nodes"
    ::route :dinsro.ui.core.networks.ln-nodes/SubPage}

   :core-networks-wallets
   {::label "Wallets"
    ::route :dinsro.ui.core.networks.wallets/SubPage}

   :core-nodes-blocks
   {::label "Blocks"
    ::route :dinsro.ui.core.nodes.blocks/SubPage}

   :core-nodes-peers
   {::label "Peers"
    ::route :dinsro.ui.core.nodes.peers/SubPage}

   :currencies-accounts
   {::label "Accounts"
    ::route :dinsro.ui.currencies.accounts/SubPage}

   :currencies-rates
   {::label "Rates"
    ::route :dinsro.ui.currencies.rates/SubPage}

   :currencies-rate-sources
   {::label "Rates Sources"
    ::route :dinsro.ui.currencies.rate-sources/SubPage}

   :home
   {::label "Home"
    ::route :dinsro.ui.home/Page}

   :ln-nodes-accounts
   {::label "Accounts"
    ::route :dinsro.ui.ln.nodes.accounts/SubPage}

   :ln-nodes-addresses
   {::label "Addresses"
    ::route :dinsro.ui.ln.nodes.addresses/SubPage}

   :ln-nodes-channels
   {::label "Channels"
    ::route :dinsro.ui.ln.nodes.channels/SubPage}

   :ln-nodes-peers
   {::label "Peers"
    ::route :dinsro.ui.ln.nodes.peers/SubPage}

   :ln-nodes-remote-nodes
   {::label "Remote Nodes"
    ::route :dinsro.ui.ln.nodes.remote-nodes/SubPage}

   :ln-nodes-wallet-addresses
   {::label "Wallet Addresses"
    ::route :dinsro.ui.ln.nodes.wallet-addresses/SubPage}

   :login
   {::auth-link? true
    ::label      "Login"
    ::route      :dinsro.ui.login/Page}

   :nodes
   {::label "Nodes"
    ::route :dinsro.ui.nodes/Dashboard}

   :nostr-connections-runs
   {::label "Runs"
    ::route :dinsro.ui.nostr.connections.runs/SubPage}

   :nostr-event-tags-relays
   {::label "Relays"
    ::route :dinsro.ui.nostr.event-tags.relays/SubPage}

   :nostr-events
   {::label "Events"
    ::route :dinsro.ui.nostr.events/Report}

   :nostr-events-relays
   {::label "Relays"
    ::route :dinsro.ui.nostr.events.relays/SubPage}

   :nostr-events-tags
   {::label "Tags"
    ::route :dinsro.ui.nostr.events.event-tags/SubPage}

   :nostr-events-witnesses
   {::label "Witnesses"
    ::route :dinsro.ui.nostr.events.witnesses/SubPage}

   :nostr-filters-items
   {::label "Items"
    ::route :dinsro.ui.nostr.filters.filter-items/SubPage}

   :nostr-pubkeys-events
   {::label "Events"
    ::route :dinsro.ui.nostr.pubkeys.events/SubPage}

   :nostr-pubkeys-items
   {::label "Filter Items"
    ::route :dinsro.ui.nostr.pubkeys.items/SubPage}

   :nostr-pubkeys-relays
   {::label "Relays"
    ::route :dinsro.ui.nostr.pubkeys.relays/SubPage}

   :registration
   {::label "Registration"
    ::route :dinsro.ui.registration/Page}

   :nostr-relays-connections
   {::label "Connections"
    ::route :dinsro.ui.nostr.relays.connections/SubPage}

   :nostr-relays-events
   {::label "Events"
    ::route :dinsro.ui.nostr.relays.events/SubPage}

   :nostr-relays-pubkeys
   {::label "Pubkeys"
    ::route :dinsro.ui.nostr.relays.pubkeys/SubPage}

   :nostr-relays-requests
   {::label "Requests"
    ::route :dinsro.ui.nostr.relays.requests/SubPage}

   :nostr-relays-runs
   {::label "Runs"
    ::route :dinsro.ui.nostr.relays.runs/SubPage}

   :nostr-relays-witnesses
   {::label "Witnesses"
    ::route :dinsro.ui.nostr.relays.witnesses/SubPage}

   :nostr-requests-connections
   {::label "Connections"
    ::route :dinsro.ui.nostr.requests.connections/SubPage}

   :nostr-requests-filters
   {::label "Filters"
    ::route :dinsro.ui.nostr.requests.filters/SubPage}

   :nostr-requests-items
   {::label "Items"
    ::route :dinsro.ui.nostr.requests.filter-items/SubPage}

   :nostr-requests-runs
   {::label "Runs"
    ::route :dinsro.ui.nostr.requests.runs/SubPage}

   :nostr-runs-witnesses
   {::label "Witnesses"
    ::route :dinsro.ui.nostr.runs.witnesses/SubPage}

   :nostr-subscriptions-pubkeys
   {::label "Pubkeys"
    ::route :dinsro.ui.nostr.subscription-pubkeys/SubPage}

   :settings
   {::label "Settings"
    ::route :dinsro.ui.settings.dashboard/Page}

   :settings-categories
   {::label "Categories"
    ::route :dinsro.ui.settings.categories/Report}

   :settings-core
   {::label "Core"
    ::route :dinsro.ui.settings.core/Dashboard}

   :settings-dashboard
   {::label "Dashboard"
    ::route :dinsro.ui.settings.dashboard/Page}

   :settings-ln
   {::label "Lightning"
    ::route :dinsro.ui.settings.ln.payments/Report}

   :settings-ln-channels
   {::label "Channels"
    ::route :dinsro.ui.settings.ln.channels/Report}

   :settings-ln-dashboard
   {::label "Dashboard"
    ::route :dinsro.ui.settings.ln.dashboard/Dashboard}

   :settings-ln-nodes
   {::label "Nodes"
    ::route :dinsro.ui.settings.ln.nodes/Report}

   :settings-ln-nodes-accounts
   {::label "Accounts"
    ::route :dinsro.ui.ln.nodes.accounts/SubPage}

   :settings-ln-nodes-addresses
   {::label "Addresses"
    ::route :dinsro.ui.ln.nodes.addresses/SubPage}

   :settings-ln-nodes-channels
   {::label "Channels"
    ::route :dinsro.ui.ln.nodes.channels/SubPage}

   :settings-ln-nodes-peers
   {::label "Peers"
    ::route :dinsro.ui.ln.nodes.peers/SubPage}

   :settings-ln-nodes-remote-nodes
   {::label "Remote Nodes"
    ::route :dinsro.ui.ln.nodes.remote-nodes/SubPage}

   :settings-ln-nodes-wallet-addresses
   {::label "Wallet Addresses"
    ::route :dinsro.ui.ln.nodes.wallet-addresses/SubPage}

   :settings-ln-payments
   {::label "Payments"
    ::route :dinsro.ui.settings.ln.payments/Report}

   :settings-ln-payreqs
   {::label "Payreqs"
    ::route :dinsro.ui.settings.ln.payreqs/Report}

   :settings-ln-remote-nodes
   {::label "Remote Nodes"
    ::route :dinsro.ui.settings.ln.remote-nodes/Report}

   :settings-rate-sources
   {::label "Rate Sources"
    ::route :dinsro.ui.settings.rate-sources/Report}

   :settings-rate-sources-accounts
   {::label "Accounts"
    ::route :dinsro.ui.settings.rate-sources.accounts/SubPage}

   :transactions
   {::label "Transactions"
    ::route :dinsro.ui.transactions/Report}})

(s/def ::id keyword?)
(s/def ::label string?)

#?(:clj
   (pc/defresolver auth-link?
     [_env props]
     {::pc/input #{::id}
      ::pc/output [::auth-link?]}
     (let [{::keys [id]} props]
       {::auth-link? (get-in routes [id ::auth-link?] false)})))

#?(:clj
   (pc/defresolver label
     [_env props]
     {::pc/input #{::id}
      ::pc/output [::label]}
     (let [{::keys [id]} props]
       {::label (get-in routes [id ::label])})))

#?(:clj
   (pc/defresolver route
     [_env props]
     {::pc/input  #{::id}
      ::pc/output [::route]}
     (let [{::keys [id]} props]
       {::route (get-in routes [id ::route])})))

(defn ident [id] {::id id})
(defn idents [ids] (map ident ids))

#?(:clj (def resolvers [auth-link? label route]))
