(ns dinsro.menus)

(def admin-accounts
  {:key "accounts" :name "Accounts" :route "dinsro.ui.admin.accounts/Report"})

(def admin-categories
  {:key "categories" :name "Categories" :route "dinsro.ui.admin.categories/Report"})

(def admin-core
  {:key "core" :name "Core" :route "dinsro.ui.admin.core.dashboard/Dashboard"})

(def admin-core-blocks
  {:key "blocks" :name "Blocks" :route "dinsro.ui.admin.core.blocks/Report"})

(def admin-core-dashboard
  {:key "dashboard" :name "Dashboard" :route "dinsro.ui.admin.core.dashboard/Dashboard"})

(def admin-core-peers
  {:key "peers" :name "Peers" :route "dinsro.ui.admin.core.peers/Report"})

(def admin-currencies
  {:key "currencies" :name "Currencies" :route "dinsro.ui.admin.currencies/Report"})

(def admin-debits
  {:key "debits" :name "Debits" :route "dinsro.ui.admin.debits/Report"})

(def admin-ln
  {:key "ln" :name "LN" :route "dinsro.ui.admin.ln/Dashboard"})

(def admin-ln-accounts
  {:key "accounts" :name "Accounts" :route "dinsro.ui.admin.ln.accounts/Report"})

(def admin-ln-channels
  {:key "channels" :name "Channels" :route "dinsro.ui.admin.ln.channels/Report"})

(def admin-ln-dashboard
  {:key "dashboard" :name "Dashboard" :route "dinsro.ui.admin.ln/Dashboard"})

(def admin-ln-invoices
  {:key "invoices" :name "Invoices" :route "dinsro.ui.admin.ln.invoices/Report"})

(def admin-ln-nodes
  {:key "nodes" :name "Nodes" :route "dinsro.ui.admin.ln.nodes/Report"})

(def admin-ln-payments
  {:key "payments" :name "Payments" :route "dinsro.ui.admin.ln.payments/Report"})

(def admin-ln-payreqs
  {:key "payreqs" :name "Payment Requests" :route "dinsro.ui.admin.ln.payreqs/Report"})

(def admin-ln-peers
  {:key "peers" :name "Peers" :route "dinsro.ui.admin.ln.peers/Report"})

(def admin-ln-remote-nodes
  {:key "remote-nodes" :name "Remote Nodes" :route "dinsro.ui.admin.ln.remote-nodes/Report"})

(def admin-nostr
  {:key "nostr" :name "Nostr" :route "dinsro.ui.admin.nostr.dashboard/Dashboard"})

(def admin-nostr-badge-acceptances
  {:key "badge-acceptances" :name "Acceptances" :route "dinsro.ui.admin.nostr.badge-acceptances/Report"})

(def admin-nostr-badge-awards
  {:key "badge-awards" :name "Awards" :route "dinsro.ui.admin.nostr.badge-awards/Report"})

(def admin-nostr-badge-definitions
  {:key "badge-definitions" :name "Definitions" :route "dinsro.ui.admin.nostr.badge-definitions/Report"})

(def admin-nostr-connections
  {:key "connections" :name "Connections" :route "dinsro.ui.admin.nostr.connections/Report"})

(def admin-nostr-connections-runs
  {:key "runs" :name "Runs" :route "dinsro.ui.nostr.connections.runs/SubPage"})

(def admin-nostr-dashboard
  {:key "dashboard" :name "dashboard" :route "dinsro.ui.admin.nostr.dashboard/Dashboard"})

(def admin-nostr-events
  {:key "events" :name "Events" :route "dinsro.ui.admin.nostr.events/Report"})

(def admin-nostr-filter-items
  {:key "filter-items" :name "Items" :route "dinsro.ui.admin.nostr.filter-items/Report"})

(def admin-nostr-filters
  {:key "filters" :name "Filters" :route "dinsro.ui.admin.nostr.filters/Report"})

(def admin-nostr-pubkeys
  {:key "pubkeys" :name "Pubkeys" :route "dinsro.ui.admin.nostr.pubkeys/Report"})

(def admin-nostr-relays
  {:key "relays" :name "Relays" :route "dinsro.ui.admin.nostr.relays/Report"})

(def admin-nostr-relays-connections
  {:key "connections" :name "Connections" :route "dinsro.ui.admin.nostr.relays.connections/SubPage"})

(def admin-nostr-relays-events
  {:key "events" :name "Events" :route "dinsro.ui.admin.nostr.relays.events/SubPage"})

(def admin-nostr-relays-pubkeys
  {:key "pubkeys" :name "Pubkeys" :route "dinsro.ui.admin.nostr.relays.pubkeys/SubPage"})

(def admin-nostr-relays-requests
  {:key "requests" :name "Requests" :route "dinsro.ui.admin.nostr.relays.requests/SubPage"})

(def admin-nostr-relays-runs
  {:key "runs" :name "Runs" :route "dinsro.ui.admin.nostr.relays.runs/SubPage"})

(def admin-nostr-relays-witnesses
  {:key "witnesses" :name "Witnesses" :route "dinsro.ui.admin.nostr.relays.witnesses/SubPage"})

(def admin-nostr-requests
  {:key "requests" :name "Requests" :route "dinsro.ui.admin.nostr.requests/Report"})

(def admin-nostr-requests-connections
  {:key "connections" :name "Connections" :route "dinsro.ui.admin.nostr.requests.connections/SubPage"})

(def admin-nostr-requests-filters
  {:key "filters" :name "Filters" :route "dinsro.ui.admin.nostr.requests.filters/SubPage"})

(def admin-nostr-requests-items
  {:key "items" :name "Items" :route "dinsro.ui.admin.nostr.requests.filter-items/SubPage"})

(def admin-nostr-requests-runs
  {:key "runs" :name "Runs" :route "dinsro.ui.admin.nostr.requests.runs/SubPage"})

(def admin-nostr-runs
  {:key "runs" :name "Runs" :route "dinsro.ui.admin.nostr.runs/Report"})

(def admin-nostr-witnesses
  {:key "witnesses" :name "Witnesses" :route "dinsro.ui.admin.nostr.witnesses/Report"})

(def admin-rate-sources
  {:key "rate-sources" :name "Rate Sources" :route "dinsro.ui.admin.rate-sources/Report"})

(def admin-rates
  {:key "rates" :name "Rates" :route "dinsro.ui.admin.rates/Report"})

(def admin-transactions
  {:key "transactions" :name "Transactions" :route "dinsro.ui.admin.transactions/Report"})

(def admin-users
  {:key "users" :name "users" :route "dinsro.ui.admin.users/Report"})

(def admin-users-accounts
  {:key "accounts" :name "Accounts" :route "dinsro.ui.admin.users.accounts/SubPage"})

(def admin-users-debits
  {:key "debits" :name "Debits" :route "dinsro.ui.admin.users.debits/SubPage"})

(def admin-users-ln-nodes
  {:key "ln-nodes" :name "LN Nodes" :route "dinsro.ui.admin.users.ln-nodes/SubPage"})

(def admin-users-pubkeys
  {:key "pubkeys" :name "Pubkeys" :route "dinsro.ui.admin.users.pubkeys/SubPage"})

(def admin-users-transactions
  {:key "transactions" :name "Transactions" :route "dinsro.ui.admin.users.transactions/SubPage"})

(def admin-users-wallets
  {:key "wallets" :name "Wallets" :route "dinsro.ui.admin.users.wallets/SubPage"})

(def core-chains-networks
  {:key "networks" :name "Networks" :route "dinsro.ui.core.chain-networks/SubPage"})

(def core-networks-addresses
  {:key "addresses" :name "Addresses" :route "dinsro.ui.core.networks.addresses/SubPage"})

(def core-networks-blocks
  {:key "blocks" :name "Blocks" :route "dinsro.ui.core.networks.blocks/SubPage"})

(def core-networks-core-nodes
  {:name "Core Nodes" :key "core-nodes" :route "dinsro.ui.core.networks.nodes/SubPage"})

(def core-networks-ln-nodes
  {:name "LN Nodes" :key "ln-nodes" :route "dinsro.ui.core.networks.ln-nodes/SubPage"})

(def core-networks-wallets
  {:name "Wallets" :key "wallets" :route "dinsro.ui.core.networks.wallets/SubPage"})

(def core-nodes-blocks
  {:key "blocks" :name "Blocks" :route "dinsro.ui.core.nodes.blocks/SubPage"})

(def core-nodes-peers
  {:key "peers" :name "Peers" :route "dinsro.ui.core.nodes.peers/SubPage"})

(def currencies-accounts
  {:key "accounts" :name "Accounts" :route "dinsro.ui.currencies.accounts/SubPage"})

(def currencies-rates
  {:key "rates" :name "Rates" :route "dinsro.ui.currencies.rates/SubPage"})

(def currencies-rate-sources
  {:key "rates-sources" :name "Rates Sources" :route "dinsro.ui.currencies.rate-sources/SubPage"})

(def ln-nodes-accounts
  {:key "accounts" :name "Accounts" :route "dinsro.ui.ln.nodes.accounts/SubPage"})

(def ln-nodes-addresses
  {:key "addresses" :name "Addresses" :route "dinsro.ui.ln.nodes.addresses/SubPage"})

(def ln-nodes-channels
  {:key "channels" :name "Channels" :route "dinsro.ui.ln.nodes.channels/SubPage"})

(def ln-nodes-peers
  {:key "peers" :name "Peers" :route "dinsro.ui.ln.nodes.peers/SubPage"})

(def ln-nodes-remote-nodes
  {:key "remote-nodes" :name "Remote Nodes" :route "dinsro.ui.ln.nodes.remote-nodes/SubPage"})

(def ln-nodes-wallet-addresses
  {:key "wallet-addresses" :name "Wallet Addresses" :route "dinsro.ui.ln.nodes.wallet-addresses/SubPage"})

(def nostr-connections-runs
  {:key "runs" :name "Runs" :route "dinsro.ui.nostr.connections.runs/SubPage"})

(def nostr-event-tags-relays
  {:key "relays" :name "Relays" :route "dinsro.ui.nostr.event-tags.relays/SubPage"})

(def nostr-events-relays
  {:key "relays" :name "Relays" :route "dinsro.ui.nostr.events.relays/SubPage"})

(def nostr-events-tags
  {:key "tags" :name "Tags" :route "dinsro.ui.nostr.events.event-tags/SubPage"})

(def nostr-events-witnesses
  {:key "witnesses" :name "Witnesses" :route "dinsro.ui.nostr.events.witnesses/SubPage"})

(def nostr-filters-items
  {:key "items" :name "Items" :route "dinsro.ui.nostr.filters.filter-items/SubPage"})

(def nostr-pubkeys-events
  {:key "events" :name "Events" :route "dinsro.ui.nostr.pubkeys.events/SubPage"})

(def nostr-pubkeys-items
  {:key "items" :name "Filter Items" :route "dinsro.ui.nostr.pubkeys.items/SubPage"})

(def nostr-pubkeys-relays
  {:key "relays" :name "Relays" :route "dinsro.ui.nostr.pubkeys.relays/SubPage"})

(def nostr-relays-connections
  {:key "connections" :name "Connections" :route "dinsro.ui.nostr.relays.connections/SubPage"})

(def nostr-relays-events
  {:key "events" :name "Events" :route "dinsro.ui.nostr.relays.events/SubPage"})

(def nostr-relays-pubkeys
  {:key "pubkeys" :name "Pubkeys" :route "dinsro.ui.nostr.relays.pubkeys/SubPage"})

(def nostr-relays-requests
  {:key "requests" :name "Requests" :route "dinsro.ui.nostr.relays.requests/SubPage"})

(def nostr-relays-runs
  {:key "runs" :name "Runs" :route "dinsro.ui.nostr.relays.runs/SubPage"})

(def nostr-relays-witnesses
  {:key "witnesses" :name "Witnesses" :route "dinsro.ui.nostr.relays.witnesses/SubPage"})

(def nostr-requests-connections
  {:key "connections" :name "Connections" :route "dinsro.ui.nostr.requests.connections/SubPage"})

(def nostr-requests-filters
  {:key "filters" :name "Filters" :route "dinsro.ui.nostr.requests.filters/SubPage"})

(def nostr-requests-items
  {:key "items" :name "Items" :route "dinsro.ui.nostr.requests.filter-items/SubPage"})

(def nostr-requests-runs
  {:key "runs" :name "Runs" :route "dinsro.ui.nostr.requests.runs/SubPage"})

(def nostr-runs-witnesses
  {:key "witnesses" :name "Witnesses" :route "dinsro.ui.nostr.runs.witnesses/SubPage"})

(def nostr-subscriptions-pubkeys
  {:key "pubkeys" :name "Pubkeys" :route "dinsro.ui.nostr.subscription-pubkeys/SubPage"})

(def settings-categories
  {:name "Categories" :key "categories" :route "dinsro.ui.settings.categories/Report"})

(def settings-core
  {:key "core" :name "Core" :route "dinsro.ui.settings.core.dashboard/Dashboard"})

(def settings-dashboard
  {:key "dashboard" :name "Dashboard" :route "dinsro.ui.settings/Dashboard"})

(def settings-ln
  {:name "Lightning" :key "ln" :route "dinsro.ui.settings.ln.payments/Report"})

(def settings-ln-channels
  {:key "channels" :name "Channels" :route "dinsro.ui.settings.ln.channels/Report"})

(def settings-ln-dashboard
  {:key "dashboard" :name "Dashboard" :route "dinsro.ui.settings/Dashboard"})

(def settings-ln-nodes
  {:key "nodes" :name "Nodes" :route "dinsro.ui.settings.ln.nodes/Report"})

(def settings-ln-nodes-accounts
  {:key "accounts" :name "Accounts" :route "dinsro.ui.ln.nodes.accounts/SubPage"})

(def settings-ln-nodes-addresses
  {:key "addresses" :name "Addresses" :route "dinsro.ui.ln.nodes.addresses/SubPage"})

(def settings-ln-nodes-channels
  {:key "channels" :name "Channels" :route "dinsro.ui.ln.nodes.channels/SubPage"})

(def settings-ln-nodes-peers
  {:key "peers" :name "Peers" :route "dinsro.ui.ln.nodes.peers/SubPage"})

(def settings-ln-nodes-remote-nodes
  {:key "remote-nodes" :name "Remote Nodes" :route "dinsro.ui.ln.nodes.remote-nodes/SubPage"})

(def settings-ln-nodes-wallet-addresses
  {:key "wallet-addresses" :name "Wallet Addresses" :route "dinsro.ui.ln.nodes.wallet-addresses/SubPage"})

(def settings-ln-payments
  {:key "payments" :name "Payments" :route "dinsro.ui.settings.ln.payments/Report"})

(def settings-ln-payreqs
  {:name "Payreqs" :key "payreqs" :route "dinsro.ui.settings.ln.payreqs/Report"})

(def settings-ln-remote-nodes
  {:name "Remote Nodes" :key "remote-nodes" :route "dinsro.ui.settings.ln.remote-nodes/Report"})

(def settings-rate-sources
  {:name "Rate Sources" :key "rate-sources" :route "dinsro.ui.settings.rate-sources/Report"})

(def settings-rate-sources-accounts
  {:key "accounts" :name "Accounts" :route "dinsro.ui.settings.rate-sources.accounts/SubPage"})

;; Menu Items

(def admin-core-menu-items
  [admin-core-dashboard
   admin-core-blocks
   admin-core-peers])

(def admin-ln-menu-items
  [admin-ln-dashboard
   admin-ln-accounts
   admin-ln-channels
   admin-ln-invoices
   admin-ln-nodes
   admin-ln-payreqs
   admin-ln-peers
   admin-ln-remote-nodes])

(def admin-menu-items
  [admin-nostr
   admin-users
   admin-core
   admin-ln
   admin-categories
   admin-accounts
   admin-currencies
   admin-transactions
   admin-debits
   admin-rate-sources
   admin-rates])

(def admin-nostr-connections-menu-items
  [admin-nostr-connections-runs])

(def admin-nostr-menu-items
  [admin-nostr-dashboard
   admin-nostr-relays
   admin-nostr-pubkeys
   admin-nostr-events
   admin-nostr-filters
   admin-nostr-badge-acceptances
   admin-nostr-badge-awards
   admin-nostr-badge-definitions
   admin-nostr-requests
   admin-nostr-connections
   admin-nostr-filter-items
   admin-nostr-runs
   admin-nostr-witnesses])

(def admin-nostr-relays-menu-items
  [admin-nostr-relays-connections
   admin-nostr-relays-requests
   admin-nostr-relays-events
   admin-nostr-relays-pubkeys
   admin-nostr-relays-runs
   admin-nostr-relays-witnesses])

(def admin-nostr-requests-menu-items
  [admin-nostr-requests-filters
   admin-nostr-requests-items
   admin-nostr-requests-runs
   admin-nostr-requests-connections])

(def admin-users-menu-items
  [admin-users-accounts
   admin-users-debits
   admin-users-ln-nodes
   admin-users-pubkeys
   admin-users-transactions
   admin-users-wallets])

(def core-chains-menu-items [core-chains-networks])

(def core-networks-menu-items
  [core-networks-addresses
   core-networks-blocks
   core-networks-ln-nodes
   core-networks-core-nodes
   core-networks-wallets])

(def core-nodes-menu-items
  [core-nodes-peers
   core-nodes-blocks])

(def currencies-menu-items
  [currencies-rate-sources
   currencies-accounts
   currencies-rates])

(def ln-nodes-menu-items
  [ln-nodes-accounts
   ln-nodes-addresses
   ln-nodes-channels
   ln-nodes-peers
   ln-nodes-remote-nodes
   ln-nodes-wallet-addresses])

(def nostr-connections-menu-items
  [nostr-connections-runs])

(def nostr-event-tags-menu-items
  [nostr-event-tags-relays])

(def nostr-events-menu-items
  [nostr-events-tags
   nostr-events-witnesses
   nostr-events-relays])

(def nostr-filters-menu-items
  [nostr-filters-items])

(def nostr-relays-menu-items
  [nostr-relays-connections
   nostr-relays-requests
   nostr-relays-events
   nostr-relays-pubkeys
   nostr-relays-runs
   nostr-relays-witnesses])

(def nostr-requests-menu-items
  [nostr-requests-filters
   nostr-requests-items
   nostr-requests-runs
   nostr-requests-connections])

(def nostr-pubkeys-menu-items
  [nostr-pubkeys-events
   nostr-pubkeys-relays
   nostr-pubkeys-items])

(def nostr-runs-menu-items
  [nostr-runs-witnesses])

(def nostr-subscriptions-menu-items
  [nostr-subscriptions-pubkeys])

(def settings-ln-menu-items
  [settings-ln-dashboard
   settings-ln-nodes
   settings-ln-channels
   settings-ln-payments
   settings-ln-payreqs
   settings-ln-remote-nodes])

(def settings-ln-nodes-menu-items
  [settings-ln-nodes-accounts
   settings-ln-nodes-addresses
   settings-ln-nodes-channels
   settings-ln-nodes-peers
   settings-ln-nodes-remote-nodes
   settings-ln-nodes-wallet-addresses])

(def settings-menu-items
  [settings-dashboard
   settings-core
   settings-ln
   settings-rate-sources
   settings-categories])

(def settings-rate-sources-menu-items
  [settings-rate-sources-accounts])
