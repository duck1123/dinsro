(ns dinsro.model.navlinks
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [lambdaisland.glogc :as log]))

;; [[../joins/navlinks.cljc]]
;; [[../mutations/navlinks.cljc]]
;; [[../ui/navlinks.cljs]]

(def routes
  {:accounts
   {::label         "Accounts"
    ::description   "An index of all accounts for a user"
    ::control       :dinsro.ui.accounts/IndexPage
    ::model-key     :dinsro.model.accounts/id
    ::parent-key    :root
    ::route         :dinsro.ui.accounts/IndexPage
    ::router        :root
    ::required-role :user}

   :accounts-show
   {::label         "Show Accounts"
    ::description   "Show page for an account"
    ::control       :dinsro.ui.accounts/ShowPage
    ::input-key     :dinsro.model.accounts/id
    ::model-key     :dinsro.model.accounts/id
    ::parent-key    :accounts
    ::route         :dinsro.ui.accounts/ShowPage
    ::router        :root
    ::required-role :user}

   :admin
   {::label         "Admin"
    ::description   "Admin root page"
    ::control       :dinsro.ui.admin/Page
    ::parent-key    :root
    ::navigate-key  :admin-users
    ::route         :dinsro.ui.admin.users/IndexPage
    ::router        :root
    ::required-role :admin}

   :admin-accounts
   {::label         "Accounts"
    ::description   "Admin page of all accounts"
    ::control       :dinsro.ui.admin.accounts/IndexPage
    ::model-key     :dinsro.model.accounts/id
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.accounts/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-accounts-show
   {::control       :dinsro.ui.admin.accounts/ShowPage
    ::description   "Admin page for account"
    ::label         "Accounts"
    ::input-key     :dinsro.model.accounts/id
    ::model-key     :dinsro.model.accounts/id
    ::parent-key    :admin-accounts
    ::route         :dinsro.ui.admin.accounts/ShowPage
    ::router        :admin
    ::required-role :admin}

   :admin-categories
   {::label         "Categories"
    ::description   "Admin page of all categories"
    ::control       :dinsro.ui.admin.categories/IndexPage
    ::model-key     :dinsro.model.accounts/categories
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.categories/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-categories-show
   {::control       :dinsro.ui.admin.categories/ShowPage
    ::description   "Admin page for category"
    ::label         "Categories"
    ::input-key     :dinsro.model.accounts/categories
    ::model-key     :dinsro.model.accounts/categories
    ::parent-key    :admin-categories
    ::route         :dinsro.ui.admin.categories/ShowPage
    ::router        :admin
    ::required-role :admin}

   :admin-core
   {::control       :dinsro.ui.admin.core/Page
    ::description   "Router page for core admin"
    ::label         "Core"
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.core.dashboard/Page
    ::router        :admin
    ::required-role :admin}

   :admin-core-addresses
   {::control       :dinsro.ui.admin.core.addresses/IndexPage
    ::description   "Admin index for core addresses"
    ::label         "Addresses"
    ::model-key     :dinsro.model.core.addresses/id
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.addresses/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-addresses-show
   {::control       :dinsro.ui.admin.core.addresses/ShowPage
    ::description   "Admin core address"
    ::label         "Show Address"
    ::input-key     :dinsro.model.core.addresses/id
    ::model-key     :dinsro.model.core.addresses/id
    ::parent-key    :admin-core-addresses
    ::route         :dinsro.ui.admin.core.addresses/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-blocks
   {::control       :dinsro.ui.admin.core.blocks/IndexPage
    ::description   "Admin index blocks"
    ::label         "Blocks"
    ::input-key     :dinsro.model.core.blocks/id
    ::model-key     :dinsro.model.core.blocks/id
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.blocks/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-blocks-show
   {::control       :dinsro.ui.admin.core.blocks/ShowPage
    ::description   "Admin show block"
    ::label         "Show Block"
    ::input-key     :dinsro.model.core.blocks/id
    ::model-key     :dinsro.model.core.blocks/id
    ::parent-key    :admin-core-blocks
    ::route         :dinsro.ui.admin.core.blocks/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-chains
   {::control       :dinsro.ui.admin.core.chains/IndexPage
    ::description   "Admin index chains"
    ::label         "Chains"
    ::model-key     :dinsro.model.core.chains/id
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.chains/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-chains-show
   {::label         "Show Chain"
    ::description   "Admin show chain"
    ::control       :dinsro.ui.admin.core.chains/ShowPage
    ::input-key     :dinsro.model.core.chains/id
    ::model-key     :dinsro.model.core.chains/id
    ::parent-key    :admin-core-chains
    ::route         :dinsro.ui.admin.core.chains/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-dashboard
   {::label         "Dashboard"
    ::description   "Admin Core Dashboard"
    ::control       :dinsro.ui.admin.core.dashboard/Page
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.dashboard/Page
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-mnemonics
   {::label         "Mnemonics"
    ::description   "Admin Index Mnemonics"
    ::control       :dinsro.ui.admin.core.mnemonics/IndexPage
    ::model-key     :dinsro.model.core.mnemonics/id
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.mnemonics/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-mnemonics-show
   {::label         "Mnemonics"
    ::description   "Admin show mnemonic"
    ::control       :dinsro.ui.admin.core.mnemonics/ShowPage
    ::input-key     :dinsro.model.core.mnemonics/id
    ::model-key     :dinsro.model.core.mnemonics/id
    ::parent-key    :admin-core-mnemonics
    ::route         :dinsro.ui.admin.core.mnemonics/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-networks
   {::label         "Networks"
    ::description   "Admin index networks"
    ::control       :dinsro.ui.admin.core.networks/IndexPage
    ::model-key     :dinsro.model.core.networks/id
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.networks/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-networks-show
   {::label         "Show Network"
    ::description   "Admin Show Network"
    ::control       :dinsro.ui.admin.core.networks/ShowPage
    ::input-key     :dinsro.model.core.networks/id
    ::model-key     :dinsro.model.core.networks/id
    ::parent-key    :admin-core-networks
    ::route         :dinsro.ui.admin.core.networks/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-nodes
   {::label         "Nodes"
    ::control       :dinsro.ui.admin.core.nodes/IndexPage
    ::description   "Admin Index Core Nodes"
    ::model-key     :dinsro.model.core.nodes/id
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.nodes/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-nodes-show
   {::control       :dinsro.ui.admin.core.nodes/ShowPage
    ::label         "Show Node"
    ::model-key     :dinsro.model.core.nodes/id
    ::navigate-key  :admin-core-nodes-show-blocks
    ::parent-key    :admin-core-nodes
    ::route         :dinsro.ui.admin.core.nodes/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-nodes-show-blocks
   {::control       :dinsro.ui.admin.core.nodes.blocks/SubPage
    ::label         "Blocks"
    ::input-key     :dinsro.model.core.nodes/id
    ::model-key     :dinsro.model.core.nodes/id
    ::parent-key    :admin-core-nodes-show
    ::route         :dinsro.ui.admin.core.nodes.blocks/SubPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-nodes-show-peers
   {::control       :dinsro.ui.admin.core.nodes.blocks/SubPage
    ::label         "Peers"
    ::input-key     :dinsro.model.core.nodes/id
    ::model-key     :dinsro.model.core.peers/id
    ::parent-key    :admin-core-nodes-show
    ::route         :dinsro.ui.admin.core.nodes.blocks/SubPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-peers
   {::control       :dinsro.ui.admin.core.peers/IndexPage
    ::label         "Peers"
    ::model-key     :dinsro.model.core.peers/id
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.peers/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-peers-show
   {::control       :dinsro.ui.admin.core.peers/ShowPage
    ::label         "Show Peer"
    ::model-key     :dinsro.model.core.peers/id
    ::parent-key    :admin-core-peers
    ::route         :dinsro.ui.admin.core.peers/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-transactions
   {::control       :dinsro.ui.admin.core.transactions/IndexPage
    ::label         "Transactions"
    ::model-key     :dinsro.model.core.transactions/id
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.transactions/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-transactions-show
   {::control       :dinsro.ui.admin.core.transactions/ShowPage
    ::label         "Show Transaction"
    ::model-key     :dinsro.model.core.transactions/id
    ::parent-key    :admin-core-transactions
    ::route         :dinsro.ui.admin.core.transactions/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-wallet-addresses
   {::control       :dinsro.ui.admin.core.wallet-addresses/IndexPage
    ::label         "Wallet Addresses"
    ::description   "Admin index of wallet addresses"
    ::model-key     :dinsro.model.core.wallet-addresses/id
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.wallet-addresses/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-wallets
   {::control       :dinsro.ui.admin.core.wallets/IndexPage
    ::description   "Admin index of wallets"
    ::label         "Wallets"
    ::model-key     :dinsro.model.core.wallets/id
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.wallets/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-wallets-show
   {::control       :dinsro.ui.admin.core.wallets/ShowPage
    ::description   "Admin Show Wallet"
    ::label         "Show Wallet"
    ::model-key     :dinsro.model.core.wallets/id
    ::parent-key    :admin-core-wallets
    ::route         :dinsro.ui.admin.core.wallets/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-words
   {::control       :dinsro.ui.admin.core.words/IndexPage
    ::label         "Words"
    ::model-key     :dinsro.model.core.words/id
    ::parent-key    :admin-core
    ::route         :dinsro.ui.admin.core.words/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-words-show
   {::control       :dinsro.ui.admin.core.words/ShowPage
    ::input-key     :dinsro.model.core.words/id
    ::label         "Show Word"
    ::model-key     :dinsro.model.core.words/id
    ::parent-key    :admin-core-words
    ::route         :dinsro.ui.admin.core.words/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-currencies
   {::control       :dinsro.ui.admin.currencies/IndexPage
    ::label         "Currencies"
    ::model-key     :dinsro.model.currencies/id
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.currencies/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-currencies-show
   {::control       :dinsro.ui.admin.currencies/ShowPage
    ::label         "Show Currency"
    ::model-key     :dinsro.model.currencies/id
    ::parent-key    :admin-currencies
    ::route         :dinsro.ui.admin.currencies/ShowPage
    ::router        :admin
    ::required-role :admin}

   :admin-debits
   {::control       :dinsro.ui.admin.debits/IndexPage
    ::label         "Debits"
    ::model-key     :dinsro.model.debits/id
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.debits/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-debits-show
   {::control       :dinsro.ui.admin.debits/ShowPage
    ::label         "Show Debit"
    ::model-key     :dinsro.model.debits/id
    ::parent-key    :admin-debits
    ::route         :dinsro.ui.admin.debits/ShowPage
    ::router        :admin
    ::required-role :admin}

   :admin-ln
   {::control       :dinsro.ui.admin.ln/Page
    ::label         "LN"
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.ln/Page
    ;; ::menu           :admin-ln
    ::router        :admin
    ::required-role :admin}

   :admin-ln-accounts
   {::control       :dinsro.ui.admin.ln.accounts/IndexPage
    ::label         "Accounts"
    ::model-key     :dinsro.model.ln.accounts/id
    ::parent-key    :admin-ln
    ::route         :dinsro.ui.admin.ln.accounts/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-accounts-show
   {::control       :dinsro.ui.admin.ln.accounts/ShowPage
    ::input-key     :dinsro.model.ln.accounts/id
    ::label         "Show Account"
    ::model-key     :dinsro.model.ln.accounts/id
    ::parent-key    :admin-ln-accounts
    ::route         :dinsro.ui.admin.ln.accounts/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-channels
   {::control       :dinsro.ui.admin.ln.channels/IndexPage
    ::label         "Channels"
    ::model-key     :dinsro.model.ln.channels/id
    ::parent-key    :admin-ln
    ::route         :dinsro.ui.admin.ln.channels/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-channels-show
   {::control       :dinsro.ui.admin.ln.channels/IndexPage
    ::input-key     :dinsro.model.ln.channels/id
    ::label         "Show Channel"
    ::model-key     :dinsro.model.ln.channels/id
    ::parent-key    :admin-ln-channels
    ::route         :dinsro.ui.admin.ln.channels/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-dashboard
   {::control       :dinsro.ui.admin.ln.dashboard/Page
    ::label         "Dashboard"
    ::parent-key    :admin-ln
    ::route         :dinsro.ui.admin.ln/Page
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-invoices
   {::control       :dinsro.ui.admin.ln.invoices/IndexPage
    ::label         "Invoices"
    ::model-key     :dinsro.model.ln.invoices/id
    ::parent-key    :admin-ln
    ::required-role :admin
    ::route         :dinsro.ui.admin.ln.invoices/IndexPage
    ::router        :admin-ln}

   :admin-ln-invoices-show
   {::control       :dinsro.ui.admin.ln.invoices/ShowPage
    ::label         "Show Invoice"
    ::model-key     :dinsro.model.ln.invoices/id
    ::parent-key    :admin-ln-invoices
    ::route         :dinsro.ui.admin.ln.invoices/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-nodes
   {::control       :dinsro.ui.admin.ln.nodes/IndexPage
    ::label         "Nodes"
    ::model-key     :dinsro.model.ln.nodes/id
    ::parent-key    :admin-ln
    ::route         :dinsro.ui.admin.ln.nodes/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-nodes-show
   {::control       :dinsro.ui.admin.ln.nodes/ShowPage
    ::input-key     :dinsro.model.ln.nodes/id
    ::label         "Show Node"
    ::model-key     :dinsro.model.ln.nodes/id
    ::parent-key    :admin-ln-nodes
    ::route         :dinsro.ui.admin.ln.nodes/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-payments
   {::control       :dinsro.ui.admin.ln.payments/IndexPage
    ::label         "Payments"
    ::model-key     :dinsro.model.ln.payments/id
    ::parent-key    :admin-ln
    ::route         :dinsro.ui.admin.ln.payments/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-payments-show
   {::control       :dinsro.ui.admin.ln.payments/ShowPage
    ::input-key     :dinsro.model.ln.payments/id
    ::label         "Show Payment"
    ::model-key     :dinsro.model.ln.payments/id
    ::parent-key    :admin-ln-payments
    ::route         :dinsro.ui.admin.ln.payments/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-payreqs
   {::control       :dinsro.ui.admin.ln.payreqs/IndexPage
    ::label         "Payment Requests"
    ::model-key     :dinsro.model.ln.payreqs/id
    ::parent-key    :admin-ln
    ::route         :dinsro.ui.admin.ln.payreqs/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-payreqs-show
   {::control       :dinsro.ui.admin.ln.payreqs/ShowPage
    ::input-key     :dinsro.model.ln.payreqs/id
    ::label         "Show Payment Request"
    ::model-key     :dinsro.model.ln.payreqs/id
    ::parent-key    :admin-ln-payreqs
    ::route         :dinsro.ui.admin.ln.payreqs/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-peers
   {::control       :dinsro.ui.admin.ln.peers/IndexPage
    ::label         "Peers"
    ::model-key     :dinsro.model.ln.peers/id
    ::parent-key    :admin-ln
    ::route         :dinsro.ui.admin.ln.peers/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-peers-show
   {::control       :dinsro.ui.admin.ln.peers/ShowPage
    ::input-key     :dinsro.model.ln.peers/id
    ::label         "Peers"
    ::model-key     :dinsro.model.ln.peers/id
    ::parent-key    :admin-ln-peers
    ::route         :dinsro.ui.admin.ln.peers/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-remote-nodes
   {::control       :dinsro.ui.admin.ln.remote-nodes/IndexPage
    ::label         "Remote Nodes"
    ::model-key     :dinsro.model.ln.remote-nodes/id
    ::parent-key    :admin-ln
    ::route         :dinsro.ui.admin.ln.remote-nodes/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-remote-nodes-show
   {::control       :dinsro.ui.admin.ln.remote-nodes/ShowPage
    ::input-key     :dinsro.model.ln.remote-nodes/id
    ::label         "Show Remote Node"
    ::model-key     :dinsro.model.ln.remote-nodes/id
    ::parent-key    :admin-ln-remote-nodes
    ::route         :dinsro.ui.admin.ln.remote-nodes/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-models
   {::control       :dinsro.ui.admin.models/IndexPage
    ::label         "Models"
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.models/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-nostr
   {::control       :dinsro.ui.admin.nostr/Page
    ::label         "Nostr"
    ::navigate-key  :admin-nostr-dashboard
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.nostr.dashboard/Page
    ;; ::menu           :admin-nostr
    ::router        :admin
    ::required-role :admin}

   :admin-nostr-badge-acceptances
   {::control       :dinsro.ui.admin.nostr.badge-acceptances/IndexPage
    ::label         "Acceptances"
    ::model-key     :dinsro.model.nostr.badge-acceptances/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.badge-acceptances/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-badge-acceptances-show
   {::control       :dinsro.ui.admin.nostr.badge-acceptances/ShowPage
    ::input-key     :dinsro.model.nostr.badge-acceptances/id
    ::label         "Acceptances"
    ::model-key     :dinsro.model.nostr.badge-acceptances/id
    ::parent-key    :admin-nostr-badge-acceptances
    ::route         :dinsro.ui.admin.nostr.badge-acceptances/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-badge-awards
   {::control       :dinsro.ui.admin.nostr.badge-awards/IndexPage
    ::label         "Awards"
    ::model-key     :dinsro.model.nostr.badge-awards/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.badge-awards/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-badge-awards-show
   {::control       :dinsro.ui.admin.nostr.badge-awards/ShowPage
    ::input-key     :dinsro.model.nostr.badge-awards/id
    ::label         "Awards"
    ::model-key     :dinsro.model.nostr.badge-awards/id
    ::parent-key    :admin-nostr-badge-awards
    ::route         :dinsro.ui.admin.nostr.badge-awards/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-badge-definitions
   {::control       :dinsro.ui.admin.nostr.badge-definitions/IndexPage
    ::label         "Definitions"
    ::model-key     :dinsro.model.nostr.badge-definitions/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.badge-definitions/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-badge-definitions-show
   {::control       :dinsro.ui.admin.nostr.badge-definitions/ShowPage
    ::label         "Definitions"
    ::model-key     :dinsro.model.nostr.badge-definitions/id
    ::parent-key    :admin-nostr-badge-definitions
    ::route         :dinsro.ui.admin.nostr.badge-definitions/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-connections
   {::control       :dinsro.ui.admin.nostr.connections/IndexPage
    ::label         "Connections"
    ::model-key     :dinsro.model.nostr.connections/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.connections/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-connections-show
   {::control       :dinsro.ui.admin.nostr.connections/ShowPage
    ::label         "Connections"
    ::model-key     :dinsro.model.nostr.connections/id
    ::parent-key    :admin-nostr-connections
    ::route         :dinsro.ui.admin.nostr.connections/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-connections-show-runs
   {::control       :dinsro.ui.nostr.connections.runs/SubPage
    ::input-key     :dinsro.model.nostr.connections/id
    ::label         "Runs"
    ::model-key     :dinsro.model.nostr.runs/id
    ::parent-key    :admin-nostr-connections-show
    ::route         :dinsro.ui.nostr.connections.runs/SubPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-dashboard
   {::control       :dinsro.ui.admin.nostr.dashboard/Page
    ::label         "dashboard"
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.dashboard/Page
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-events
   {::control       :dinsro.ui.admin.nostr.events/IndexPage
    ::label         "Events"
    ::description   "Admin index of events"
    ::model-key     :dinsro.model.nostr.events/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.events/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-events-show
   {::control       :dinsro.ui.admin.nostr.events/ShowPage
    ::label         "Show Event"
    ::input-key     :dinsro.model.nostr.events/id
    ::description   "Admin page for an event"
    ::model-key     :dinsro.model.nostr.events/id
    ::parent-key    :admin-nostr-events
    ::route         :dinsro.ui.admin.nostr.events/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-filter-items
   {::control       :dinsro.ui.admin.nostr.filter-items/IndexPage
    ::label         "Items"
    ::model-key     :dinsro.model.nostr.filter-items/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.filter-items/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-filter-items-show
   {::control       :dinsro.ui.admin.nostr.filter-items/ShowPage
    ::input-key     :dinsro.model.nostr.filter-items/id
    ::label         "Items"
    ::model-key     :dinsro.model.nostr.filter-items/id
    ::parent-key    :admin-nostr-filter-items
    ::route         :dinsro.ui.admin.nostr.filter-items/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-filters
   {::control       :dinsro.ui.admin.nostr.filters/IndexPage
    ::label         "Filters"
    ::model-key     :dinsro.model.nostr.filters/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.filters/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-filters-show
   {::control       :dinsro.ui.admin.nostr.filters/ShowPage
    ::input-key     :dinsro.model.nostr.filters/id
    ::label         "Show Filter"
    ::model-key     :dinsro.model.nostr.filters/id
    ::parent-key    :admin-nostr-filters
    ::route         :dinsro.ui.admin.nostr.filters/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-pubkeys
   {::control       :dinsro.ui.admin.nostr.pubkeys/IndexPage
    ::label         "Pubkeys"
    ::model-key     :dinsro.model.nostr.pubkeys/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.pubkeys/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-pubkeys-show
   {::control       :dinsro.ui.admin.nostr.pubkeys/ShowPage
    ::input-key     :dinsro.model.nostr.pubkeys/id
    ::label         "Show Pubkey"
    ::model-key     :dinsro.model.nostr.pubkeys/id
    ::parent-key    :admin-nostr-pubkeys
    ::route         :dinsro.ui.admin.nostr.pubkeys/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-relays
   {::control       :dinsro.ui.admin.nostr.relays/IndexPage
    ::label         "Relays"
    ::description   "Admin index of relays"
    ::model-key     :dinsro.model.nostr.relays/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.relays/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-relays-show
   {::control       :dinsro.ui.admin.nostr.relays/ShowPage
    ::label         "Show Relay"
    ::input-key     :dinsro.model.nostr.relays/id
    ::model-key     :dinsro.model.nostr.relays/id
    ::parent-key    :admin-nostr-relays
    ::route         :dinsro.ui.admin.nostr.relays/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-relays-show-connections
   {::control       :dinsro.ui.admin.nostr.relays.connections/SubPage
    ::input-key     :dinsro.model.nostr.relays/id
    ::label         "Connections"
    ::model-key     :dinsro.model.nostr.connections/id
    ::parent-key    :admin-nostr-relays-show
    ::route         :dinsro.ui.admin.nostr.relays.connections/SubPage
    ::router        :admin-nostr-relays
    ::required-role :admin}

   :admin-nostr-relays-show-events
   {::control       :dinsro.ui.admin.nostr.relays.events/SubPage
    ::input-key     :dinsro.model.nostr.relays/id
    ::label         "Events"
    ::model-key     :dinsro.model.nostr.events/id
    ::parent-key    ::admin-nostr-relays-show
    ::route         :dinsro.ui.admin.nostr.relays.events/SubPage
    ::router        :admin-nostr-relays
    ::required-role :admin}

   :admin-nostr-relays-show-pubkeys
   {::control       :dinsro.ui.admin.nostr.relays.pubkeys/SubPage
    ::input-key     :dinsro.model.nostr.relays/id
    ::label         "Pubkeys"
    ::model-key     :dinsro.model.nostr.pubkeys/id
    ::parent-key    :admin-nostr-relays-show
    ::route         :dinsro.ui.admin.nostr.relays.pubkeys/SubPage
    ::router        :admin-nostr-relays
    ::required-role :admin}

   :admin-nostr-relays-show-requests
   {::control       :dinsro.ui.admin.nostr.relays.requests/SubPage
    ::input-key     :dinsro.model.nostr.relays/id
    ::label         "Requests"
    ::model-key     :dinsro.model.nostr.requests/id
    ::parent-key    :admin-nostr-relays-show
    ::route         :dinsro.ui.admin.nostr.relays.requests/SubPage
    ::router        :admin-nostr-relays
    ::required-role :admin}

   :admin-nostr-relays-show-runs
   {::control       :dinsro.ui.admin.nostr.relays.runs/SubPage
    ::input-key     :dinsro.model.nostr.relays/id
    ::label         "Runs"
    ::model-key     :dinsro.model.nostr.runs/id
    ::parent-key    :admin-nostr-relays-show
    ::route         :dinsro.ui.admin.nostr.relays.runs/SubPage
    ::router        :admin-nostr-relays
    ::required-role :admin}

   :admin-nostr-relays-show-witnesses
   {::control       :dinsro.ui.admin.nostr.relays.witnesses/SubPage
    ::input-key     :dinsro.model.nostr.relays/id
    ::label         "Witnesses"
    ::model-key     :dinsro.model.nostr.witnesses/id
    ::parent-key    :admin-nostr-relays-show
    ::route         :dinsro.ui.admin.nostr.relays.witnesses/SubPage
    ::router        :admin-nostr-relays
    ::required-role :admin}

   :admin-nostr-requests
   {::control       :dinsro.ui.admin.nostr.requests/IndexPage
    ::label         "Requests"
    ::model-key     :dinsro.model.nostr.requests/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.requests/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-requests-show
   {::control       :dinsro.ui.admin.nostr.requests/ShowPage
    ::label         "Show Request"
    ::input-key     :dinsro.model.nostr.requests/id
    ::model-key     :dinsro.model.nostr.requests/id
    ::parent-key    :admin-nostr-requests
    ::route         :dinsro.ui.admin.nostr.requests/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-requests-show-connections
   {::control       :dinsro.ui.admin.nostr.requests.connections/SubPage
    ::input-key     :dinsro.model.nostr.requests/id
    ::label         "Connections"
    ::model-key     :dinsro.model.nostr.connections/id
    ::parent-key    :admin-nostr-requests-show
    ::route         :dinsro.ui.admin.nostr.requests.connections/SubPage
    ::router        :admin-nostr-requests
    ::required-role :admin}

   :admin-nostr-requests-show-filters
   {::control       :dinsro.ui.admin.nostr.requests.filters/SubPage
    ::input-key     :dinsro.model.nostr.requests/id
    ::label         "Filters"
    ::model-key     :dinsro.model.nostr.filters/id
    ::parent-key    :admin-nostr-requests-show
    ::route         :dinsro.ui.admin.nostr.requests.filters/SubPage
    ::router        :admin-nostr-requests
    ::required-role :admin}

   :admin-nostr-requests-show-filter-items
   {::control       :dinsro.ui.admin.nostr.requests.filter-items/SubPage
    ::input-key     :dinsro.model.nostr.requests/id
    ::label         "Items"
    ::model-key     :dinsro.model.nostr.filter-items/id
    ::parent-key    :admin-nostr-requests-show
    ::route         :dinsro.ui.admin.nostr.requests.filter-items/SubPage
    ::required-role :admin}

   :admin-nostr-requests-show-runs
   {::control       :dinsro.ui.admin.nostr.requests.runs/SubPage
    ::input-key     :dinsro.model.nostr.requests/id
    ::label         "Runs"
    ::model-key     :dinsro.model.nostr.runs/id
    ::parent-key    :admin-nostr-requests-show
    ::route         :dinsro.ui.admin.nostr.requests.runs/SubPage
    ::router        :admin-nostr-requests
    ::required-role :admin}

   :admin-nostr-runs
   {::control       :dinsro.ui.admin.nostr.runs/IndexPage
    ::label         "Runs"
    ::model-key     :dinsro.model.nostr.runs/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.runs/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-witnesses
   {::control       :dinsro.ui.admin.nostr.witnesses/IndexPage
    ::label         "Witnesses"
    ::model-key     :dinsro.model.nostr.runs/id
    ::parent-key    :admin-nostr
    ::route         :dinsro.ui.admin.nostr.witnesses/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-witnesses-show
   {::control       :dinsro.ui.admin.nostr.witnesses/ShowPage
    ::label         "Show Witness"
    ::input-key     :dinsro.model.nostr.witnesses/id
    ::model-key     :dinsro.model.nostr.witnesses/id
    ::parent-key    :admin-nostr-witnesses
    ::route         :dinsro.ui.admin.nostr.witnesses/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-rate-sources
   {::control       :dinsro.ui.admin.rate-sources/IndexPage
    ::label         "Rate Sources"
    ::model-key     :dinsro.model.rate-sources/id
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.rate-sources/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-rates
   {::control       :dinsro.ui.admin.rates/IndexPage
    ::label         "Rates"
    ::model-key     :dinsro.model.rates/id
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.rates/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-transactions
   {::control       :dinsro.ui.admin.transactions/IndexPage
    ::label         "Transactions"
    ::model-key     :dinsro.model.transactions/id
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.transactions/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-users
   {::control       :dinsro.ui.admin.users/IndexPage
    ::label         "Users"
    ::model-key     :dinsro.model.users/id
    ::navigate-key  :admin-users-accounts
    ::parent-key    :admin
    ::route         :dinsro.ui.admin.users/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-users-show
   {::control       :dinsro.ui.admin.users/ShowPage
    ::label         "Admin Show User"
    ::input-key     :dinsro.model.users/id
    ::model-key     :dinsro.model.users/id
    ::parent-key    :admin-users
    ::route         :dinsro.ui.admin.users/ShowPage
    ::router        :admin
    ::required-role :admin
    ::target        :dinsro.model.users/id}

   :admin-users-show-accounts
   {::control       :dinsro.ui.admin.users.accounts/SubPage
    ::label         "Accounts"
    ::model-key     :dinsro.model.accounts/id
    ::parent-key    :admin-users
    ::route         :dinsro.ui.admin.users.accounts/SubPage
    ::router        :admin-users
    ::required-role :admin}

   :admin-users-show-categories
   {::control       :dinsro.ui.admin.users.categories/SubPage
    ::label         "Categories"
    ::model-key     :dinsro.model.categories/id
    ::parent-key    :admin-users-show
    ::route         :dinsro.ui.admin.users.categories/SubPage
    ::router        :admin-users
    ::required-role :admin}

   :admin-users-show-debits
   {::control       :dinsro.ui.admin.users.debits/SubPage
    ::label         "Debits"
    ::model-key     :dinsro.model.debits/id
    ::parent-key    :admin-users-show
    ::route         :dinsro.ui.admin.users.debits/SubPage
    ::router        :admin-users
    ::required-role :admin}

   :admin-users-show-ln-nodes
   {::control       :dinsro.ui.admin.users.ln-nodes/SubPage
    ::label         "LN Nodes"
    ::model-key     :dinsro.model.ln.nodes/id
    ::parent-key    :admin-users-show
    ::route         :dinsro.ui.admin.users.ln-nodes/SubPage
    ::router        :admin-users
    ::required-role :admin}

   :admin-users-show-pubkeys
   {::control       :dinsro.ui.admin.users.pubkeys/SubPage
    ::label         "Pubkeys"
    ::model-key     :dinsro.model.nostr.pubkeys/id
    ::parent-key    :admin-users-show
    ::route         :dinsro.ui.admin.users.pubkeys/SubPage
    ::router        :admin-users
    ::required-role :admin}

   :admin-users-show-transactions
   {::control       :dinsro.ui.admin.users.transactions/SubPage
    ::label         "Transactions"
    ::model-key     :dinsro.model.transactions/id
    ::parent-key    :admin-users-show
    ::route         :dinsro.ui.admin.users.transactions/SubPage
    ::router        :admin-users
    ::required-role :admin}

   :admin-users-show-user-pubkeys
   {::control       :dinsro.ui.admin.users.user-pubkeys/SubPage
    ::label         "User Pubkeys"
    ::model-key     :dinsro.model.nostr.pubkeys/id
    ::parent-key    :admin-users-show
    ::route         :dinsro.ui.admin.users.user-pubkeys/SubPage
    ::router        :admin-users
    ::required-role :admin}

   :admin-users-show-wallets
   {::control       :dinsro.ui.admin.users.wallets/SubPage
    ::label         "Wallets"
    ::model-key     :dinsro.model.core.wallets/id
    ::parent-key    :admin-users-show
    ::route         :dinsro.ui.admin.users.wallets/SubPage
    ::router        :admin-users
    ::required-role :admin}

   :contacts
   {::control       :dinsro.ui.contacts/IndexPage
    ::label         "Contacts"
    ::model-key     :dinsro.model.contacts/id
    ::parent-key    :root
    ::route         :dinsro.ui.contacts/IndexPage
    ::router        :root
    ::required-role :user}

   :core
   {::control       :dinsro.ui.core/Page
    ::description   "Router for core"
    ::label         "Core"
    ::parent-key    :root
    ::route         :dinsro.ui.core.dashboard/Page
    ::router        :route
    ::required-role :guest}

   :core-blocks-show
   {::control       :dinsro.ui.core.blocks/ShowPage
    ::label         "Show Block"
    ::input-key     :dinsro.model.core.blocks/id
    ::model-key     :dinsro.model.core.blocks/id
    ::parent-key    :core
    ::route         :dinsro.ui.core.blocks/ShowPage
    ::router        :core
    ::required-role :user}

   :core-chains-show
   {::control       :dinsro.ui.core.chains/ShowPage
    ::label         "Show Chain"
    ::model-key     :dinsro.model.core.chains/id
    ::navigate-key  :core-chains-show-networks
    ::parent-key    :core
    ::route         :dinsro.ui.core.chains/ShowPage
    ::router        :core
    ::required-role :user}

   :core-chains-show-networks
   {::control       :dinsro.ui.core.chain-networks/SubPage
    ::label         "Networks"
    ::model-key     :dinsro.model.core.networks/id
    ::parent-key    :core-chains-show
    ::route         :dinsro.ui.core.chain-networks/SubPage
    ::router        :core
    ::required-role :user}

   :core-networks-show
   {::control       :dinsro.ui.core.networks/ShowPage
    ::input-key     :dinsro.model.core.networks/id
    ::label         "Show Network"
    ::model-key     :dinsro.model.core.networks/id
    ::parent-key    :core-networks
    ::route         :dinsro.ui.core.networks/ShowPage
    ::router        :core
    ::required-role :user}

   :core-networks-show-addresses
   {::control       :dinsro.ui.core.networks.addresses/SubPage
    ::label         "Addresses"
    ::model-key     :dinsro.model.core.addresses/id
    ::parent-key    :core-networks-show
    ::route         :dinsro.ui.core.networks.addresses/SubPage
    ::router        :core-networks
    ::required-role :user}

   :core-networks-show-blocks
   {::control       :dinsro.ui.core.networks.blocks/SubPage
    ::label         "Blocks"
    ::model-key     :dinsro.model.core.blocks/id
    ::parent-key    :core-networks-show
    ::route         :dinsro.ui.core.networks.blocks/SubPage
    ::router        :core-networks
    ::required-role :user}

   :core-networks-show-core-nodes
   {::control       :dinsro.ui.core.networks.nodes/SubPage
    ::label         "Core Nodes"
    ::model-key     :dinsro.model.core.nodes/id
    ::parent-key    :core-networks-show
    ::route         :dinsro.ui.core.networks.nodes/SubPage
    ::router        :core-networks
    ::required-role :user}

   :core-networks-show-ln-nodes
   {::control       :dinsro.ui.core.networks.ln-nodes/SubPage
    ::label         "LN Nodes"
    ::model-key     :dinsro.model.ln.nodes/id
    ::parent-key    :core-networks-show
    ::route         :dinsro.ui.core.networks.ln-nodes/SubPage
    ::router        :core-networks
    ::required-role :user}

   :core-networks-show-wallets
   {::control       :dinsro.ui.core.networks.wallets/SubPage
    ::label         "Wallets"
    ::model-key     :dinsro.model.core.wallets/id
    ::parent-key    :core-networks-show
    ::route         :dinsro.ui.core.networks.wallets/SubPage
    ::router        :core-networks
    ::required-role :user}

   :core-nodes
   {::control       :dinsro.ui.core.nodes/IndexPage
    ::label         "Index Nodes"
    ::model-key     :dinsro.model.core.nodes/id
    ::parent-key    :core
    ::route         :dinsro.ui.core.nodes/IndexPage
    ::router        :core
    ::required-role :user}

   :core-nodes-show
   {::control       :dinsro.ui.core.nodes/ShowPage
    ::label         "Show Node"
    ::input-key     :dinsro.model.core.nodes/id
    ::model-key     :dinsro.model.core.nodes/id
    ::parent-key    :core
    ::route         :dinsro.ui.core.nodes/ShowPage
    ::router        :core
    ::required-role :user}

   :core-nodes-show-blocks
   {::control       :dinsro.ui.core.nodes.blocks/SubPage
    ::label         "Blocks"
    ::model-key     :dinsro.model.core.blocks/id
    ::parent-key    :core-nodes-show
    ::route         :dinsro.ui.core.nodes.blocks/SubPage
    ::router        :core-nodes
    ::required-role :user}

   :core-nodes-show-peers
   {::control       :dinsro.ui.core.nodes.peers/SubPage
    ::label         "Peers"
    ::model-key     :dinsro.model.core.peers/id
    ::parent-key    :core-nodes-show
    ::route         :dinsro.ui.core.nodes.peers/SubPage
    ::router        :core-nodes
    ::required-role :user}

   :core-transactions-show
   {::control       :dinsro.ui.core.transactions/ShowPage
    ::label         "Show Transaction"
    ::input-key     :dinsro.model.core.transactions/id
    ::model-key     :dinsro.model.core.transactions/id
    ::parent-key    :core
    ::route         :dinsro.ui.core.transactions/ShowPage
    ::router        :core
    ::required-role :user}

   :core-wallets-show
   {::control       :dinsro.ui.core.wallets/ShowPage
    ::label         "Show Wallet"
    ::input-key     :dinsro.model.core.wallets/id
    ::model-key     :dinsro.model.core.wallets/id
    ::parent-key    :core
    ::route         :dinsro.ui.core.wallets/ShowPage
    ::router        :core
    ::required-role :user}

   :currencies
   {::control       :dinsro.ui.currencies/IndexPage
    ::label         "Currencies"
    ::model-key     :dinsro.model.currencies/id
    ::parent-key    :root
    ::route         :dinsro.ui.currencies/IndexPage
    ::router        :root
    ::required-role :user}

   :currencies-show
   {::control       :dinsro.ui.currencies/ShowPage
    ::label         "Show Currency"
    ::input-key     :dinsro.model.currencies/id
    ::model-key     :dinsro.model.currencies/id
    ::parent-key    :currencies
    ::route         :dinsro.ui.currencies/ShowPage
    ::router        :root
    ::required-role :user}

   :currencies-show-accounts
   {::control       :dinsro.ui.currencies.accounts/SubPage
    ::label         "Accounts"
    ::input-key     :dinsro.model.currencies/id
    ::model-key     :dinsro.model.accounts/id
    ::parent-key    :currencies-show
    ::route         :dinsro.ui.currencies.accounts/SubPage
    ::router        :currencies
    ::required-role :user}

   :currencies-show-rates
   {::control       :dinsro.ui.currencies.rates/SubPage
    ::label         "Rates"
    ::input-key     :dinsro.model.currencies/id
    ::model-key     :dinsro.model.rates/id
    ::parent-key    :currencies-show
    ::route         :dinsro.ui.currencies.rates/SubPage
    ::router        :currencies
    ::required-role :user}

   :currencies-show-rate-sources
   {::control       :dinsro.ui.currencies.rate-sources/SubPage
    ::label         "Rates Sources"
    ::input-key     :dinsro.model.currencies/id
    ::model-key     :dinsro.model.rate-sources/id
    ::parent-key    :currencies-show
    ::route         :dinsro.ui.currencies.rate-sources/SubPage
    ::router        :currencies
    ::required-role :user}

   :home
   {::control       :dinsro.ui.home/Page
    ::label         "Home"
    ::parent-key    :root
    ::route         :dinsro.ui.home/Page
    ::router        :root
    ::required-role :guest}

   :ln
   {::control       :dinsro.ui.ln/Page
    ::label         "LN Router"
    ::navigate-key  :ln-nodes
    ::parent-key    :root
    ::route         :dinsro.ui.ln/Page
    ::router        :root
    ::required-role :user}

   :ln-nodes
   {::control       :dinsro.ui.ln.nodes/ShowPage
    ::label         "Index LN Nodes"
    ::model-key     :dinsro.model.ln.nodes/id
    ::parent-key    :ln
    ::route         :dinsro.ui.ln.nodes/ShowPage
    ::router        :ln
    ::required-role :user}

   :ln-nodes-show
   {::control       :dinsro.ui.ln.nodes/ShowPage
    ::label         "Show Node"
    ::model-key     :dinsro.model.ln.nodes/id
    ::parent-key    :ln-nodes
    ::route         :dinsro.ui.ln.nodes/ShowPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-show-accounts
   {::control       :dinsro.ui.ln.nodes.accounts/SubPage
    ::label         "Accounts"
    ::model-key     :dinsro.model.ln.accounts/id
    ::parent-key    :ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.accounts/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-show-addresses
   {::control       :dinsro.ui.ln.nodes.addresses/SubPage
    ::label         "Addresses"
    ::model-key     :dinsro.model.ln.addresses/id
    ::parent-key    :ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.addresses/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-show-channels
   {::control       :dinsro.ui.ln.nodes.channels/SubPage
    ::label         "Channels"
    ::model-key     :dinsro.model.ln.channels/id
    ::parent-key    :ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.channels/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-show-peers
   {::control       :dinsro.ui.ln.nodes.peers/SubPage
    ::label         "Peers"
    ::model-key     :dinsro.model.ln.peers/id
    ::parent-key    :ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.peers/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-show-remote-nodes
   {::control       :dinsro.ui.ln.nodes.remote-nodes/SubPage
    ::label         "Remote Nodes"
    ::input-key     :dinsro.model.ln.remote-nodes/id
    ::model-key     :dinsro.model.ln.remote-nodes/id
    ::parent-key    :ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.remote-nodes/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-show-wallet-addresses
   {::control       :dinsro.ui.ln.nodes.wallet-addresses/SubPage
    ::label         "Wallet Addresses"
    ::model-key     :dinsro.model.ln.wallet-addresses/id
    ::parent-key    :ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.wallet-addresses/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-remote-nodes-show
   {::control       :dinsro.ui.ln.remote-nodes/ShowPage
    ::label         "Show Remote Node"
    ::input-key     :dinsro.model.ln.remote-nodes/id
    ::model-key     :dinsro.model.ln.remote-nodes/id
    ::parent-key    :ln
    ::route         :dinsro.ui.ln.remote-nodes/ShowPage
    ::router        :ln
    ::required-role :user}

   :login
   {::control       :dinsro.ui.login/Page
    ::auth-link?    true
    ::label         "Login"
    ::parent-key    :root
    ::route         :dinsro.ui.login/Page
    ::required-role :guest
    ::router        :root}

   :navbars
   {::control       :dinsro.ui.navbars/IndexPage
    ::label         "Navbars"
    ::model-key     :dinsro.model.navbars/id
    ::parent-key    :root
    ::route         :dinsro.ui.navbars/IndexPage
    ::router        :root
    ::required-role :user}

   :navlinks
   {::control       :dinsro.ui.navlinks/IndexPage
    ::label         "Navlinks"
    ::model-key     ::id
    ::parent-key    :root
    ::route         :dinsro.ui.navlinks/IndexPage
    ::router        :root
    ::required-role :user}

   :nodes
   {::control       :dinsro.ui.nodes/Page
    ::label         "Nodes"
    ::parent-key    :root
    ::route         :dinsro.ui.nodes/Page
    ::required-role :user
    ::router        :root}

   :nostr
   {::control       :dinsro.ui.nostr.events/IndexPage
    ::label         "Nostr"
    ::model-key     :dinsro.model.nostr.events/id
    ::parent-key    :root
    ::route         :dinsro.ui.nostr.events/IndexPage
    ::required-role :user
    ::router        :root}

   :nostr-connections
   {::control       :dinsro.ui.nostr.connections.runs/SubPage
    ::label         "Connections"
    ::model-key     :dinsro.model.nostr.connections/id
    ::parent-key    :nostr
    ::route         :dinsro.ui.nostr.connections.runs/SubPage
    ::router        :nostr
    ::required-role :user}

   :nostr-connections-show
   {::control       :dinsro.ui.nostr.connections/ShowPage
    ::label         "Show Connection"
    ::input-key     :dinsro.model.nostr.connections/id
    ::model-key     :dinsro.model.nostr.connections/id
    ::parent-key    :nostr-connections
    ::route         :dinsro.ui.nostr.connections/ShowPage
    ::router        :nostr
    ::required-role :user}

   :nostr-connections-show-runs
   {::control       :dinsro.ui.nostr.connections.runs/SubPage
    ::label         "Runs"
    ::model-key     :dinsro.model.nostr.runs/id
    ::parent-key    :nostr-connections-show
    ::route         :dinsro.ui.nostr.connections.runs/SubPage
    ::router        :nostr-connections
    ::required-role :user}

   :nostr-event-tags-relays
   {::control       :dinsro.ui.nostr.event-tags.relays/SubPage
    ::label         "Relays"
    ::model-key     :dinsro.model.nostr.relays/id
    ::parent-key    :nostr-event-tags-show
    ::route         :dinsro.ui.nostr.event-tags.relays/SubPage
    ::router        :nostr-event-tags
    ::required-role :user}

   :nostr-event-tags-show
   {::control       :dinsro.ui.nostr.event-tags/ShowPage
    ::label         "Show Event Tag"
    ::input-key     :dinsro.model.nostr.event-tags/id
    ::model-key     :dinsro.model.nostr.event-tags/id
    ::parent-key    :nostr-event-tags
    ::route         :dinsro.ui.nostr.event-tags.relays/SubPage
    ::router        :nostr-event-tags
    ::required-role :user}

   :nostr-events
   {::control       :dinsro.ui.nostr.events/IndexPage
    ::label         "Events"
    ::model-key     :dinsro.model.nostr.events/id
    ::parent-key    :nostr
    ::route         :dinsro.ui.nostr.events/IndexPage
    ::router        :nostr
    ::required-role :user}

   :nostr-events-show-relays
   {::control       :dinsro.ui.nostr.events.relays/SubPage
    ::label         "Relays"
    ::model-key     :dinsro.model.nostr.relays/id
    ::parent-key    :nostr-events-show
    ::route         :dinsro.ui.nostr.events.relays/SubPage
    ::router        :nostr-events
    ::required-role :user}

   :nostr-events-show-tags
   {::control       :dinsro.ui.nostr.events.event-tags/SubPage
    ::label         "Tags"
    ::model-key     :dinsro.model.nostr.event-tags/id
    ::parent-key    :nostr-event-show
    ::route         :dinsro.ui.nostr.events.event-tags/SubPage
    ::router        :nostr-events
    ::required-role :user}

   :nostr-events-show-witnesses
   {::control       :dinsro.ui.nostr.events.witnesses/SubPage
    ::label         "Witnesses"
    ::model-key     :dinsro.model.nostr.witnesses/id
    ::parent-key    :nostr-event-show
    ::route         :dinsro.ui.nostr.events.witnesses/SubPage
    ::router        :nostr-events
    ::required-role :user}

   :nostr-filters
   {::control       :dinsro.ui.nostr.filters/IndexPage
    ::label         "Filters"
    ::model-key     :dinsro.model.nostr.filters/id
    ::parent-key    :nostr
    ::route         :dinsro.ui.nostr.filters/IndexPage
    ::router        :nostr
    ::required-role :user}

   :nostr-filters-show
   {::control       :dinsro.ui.nostr.filters/SHowPage
    ::label         "Show Filter"
    ::input-key     :dinsro.model.nostr.filters/id
    ::model-key     :dinsro.model.nostr.filters/id
    ::parent-key    :nostr-filters
    ::route         :dinsro.ui.nostr.filters/SHowPage
    ::router        :nostr
    ::required-role :user}

   :nostr-filters-show-filter-items
   {::control       :dinsro.ui.nostr.filters.filter-items/SubPage
    ::label         "Items"
    ::input-key     :dinsro.model.nostr.filters/id
    ::model-key     :dinsro.model.nostr.filter-items/id
    ::parent-key    :nostr-filters-show
    ::route         :dinsro.ui.nostr.filters.filter-items/SubPage
    ::router        :nostr-filters
    ::required-role :user}

   :nostr-filters-show-items
   {::control       :dinsro.ui.nostr.filters.filter-items/SubPage
    ::label         "Items"
    ::input-key     :dinsro.model.nostr.filters/id
    ::model-key     :dinsro.model.nostr.filter-items/id
    ::parent-key    :nostr-filters-show
    ::route         :dinsro.ui.nostr.filters.filter-items/SubPage
    ::router        :nostr-filters
    ::required-role :user}

   :nostr-pubkeys
   {::control       :dinsro.ui.nostr.pubkeys/IndexPage
    ::label         "Pubkeys"
    ::model-key     :dinsro.model.nostr.pubkeys/id
    ::parent-key    :nostr
    ::route         :dinsro.ui.nostr.pubkeys/IndexPage
    ::router        :nostr
    ::required-role :user}

   :nostr-pubkeys-show
   {::control       :dinsro.ui.nostr.pubkeys/ShowPage
    ::label         "Show Pubkey"
    ::input-key     :dinsro.model.nostr.pubkeys/id
    ::model-key     :dinsro.model.nostr.pubkeys/id
    ::parent-key    :nostr-pubkeys
    ::route         :dinsro.ui.nostr.pubkeys/ShowPage
    ::router        :nostr
    ::required-role :user}

   :nostr-pubkeys-show-events
   {::control       :dinsro.ui.nostr.pubkeys.events/SubPage
    ::label         "Events"
    ::model-key     :dinsro.model.nostr.events/id
    ::parent-key    :nostr-pubkeys-show
    ::route         :dinsro.ui.nostr.pubkeys.events/SubPage
    ::router        :nostr-pubkeys
    ::required-role :user}

   :nostr-pubkeys-show-items
   {::control       :dinsro.ui.nostr.pubkeys.items/SubPage
    ::label         "Filter Items"
    ::model-key     :dinsro.model.nostr.items/id
    ::parent-key    :nostr-pubkeys-show
    ::route         :dinsro.ui.nostr.pubkeys.items/SubPage
    ::router        :nostr-pubkeys
    ::required-role :user}

   :nostr-pubkeys-show-relays
   {::control       :dinsro.ui.nostr.pubkeys.relays/SubPage
    ::label         "Relays"
    ::model-key     :dinsro.model.nostr.relays/id
    ::parent-key    :nostr-pubkeys-show
    ::route         :dinsro.ui.nostr.pubkeys.relays/SubPage
    ::router        :nostr-pubkeys
    ::required-role :user}

   :nostr-relays-show
   {::control       :dinsro.ui.nostr.relays/ShowPage
    ::label         "Show Relay"
    ::input-key     :dinsro.model.nostr.relays/id
    ::model-key     :dinsro.model.nostr.relays/id
    ::parent-key    :nostr
    ::route         :dinsro.ui.nostr.relays/ShowPage
    ::router        :nostr
    ::required-role :user}

   :nostr-relays-show-connections
   {::control       :dinsro.ui.nostr.relays.connections/SubPage
    ::label         "Connections"
    ::model-key     :dinsro.model.nostr.connections/id
    ::parent-key    :nostr-pubkeys-show
    ::route         :dinsro.ui.nostr.relays.connections/SubPage
    ::router        :nostr-relays
    ::required-role :user}

   :nostr-relays-show-events
   {::control       :dinsro.ui.nostr.relays.events/SubPage
    ::label         "Events"
    ::model-key     :dinsro.model.nostr.events/id
    ::parent-key    :nostr-relays-show
    ::route         :dinsro.ui.nostr.relays.events/SubPage
    ::router        :nostr-relays
    ::required-role :user}

   :nostr-relays-show-pubkeys
   {::control       :dinsro.ui.nostr.relays.pubkeys/SubPage
    ::label         "Pubkeys"
    ::model-key     :dinsro.model.nostr.pubkeys/id
    ::parent-key    :nostr-relays-show
    ::route         :dinsro.ui.nostr.relays.pubkeys/SubPage
    ::router        :nostr-relays
    ::required-role :user}

   :nostr-relays-show-requests
   {::control       :dinsro.ui.nostr.relays.requests/SubPage
    ::label         "Requests"
    ::model-key     :dinsro.model.nostr.requests/id
    ::parent-key    :nostr-relays-show
    ::route         :dinsro.ui.nostr.relays.requests/SubPage
    ::router        :nostr-relays
    ::required-role :user}

   :nostr-relays-show-runs
   {::control       :dinsro.ui.nostr.relays.runs/SubPage
    ::label         "Runs"
    ::model-key     :dinsro.model.nostr.runs/id
    ::parent-key    :nostr-relays-show
    ::route         :dinsro.ui.nostr.relays.runs/SubPage
    ::router        :nostr-relays
    ::required-role :user}

   :nostr-relays-show-witnesses
   {::control       :dinsro.ui.nostr.relays.witnesses/SubPage
    ::label         "Witnesses"
    ::model-key     :dinsro.model.nostr.witnesses/id
    ::parent-key    :nostr-relays-show
    ::route         :dinsro.ui.nostr.relays.witnesses/SubPage
    ::router        :nostr-relays
    ::required-role :user}

   :nostr-requests-show
   {::control       :dinsro.ui.nostr.requests/ShowPage
    ::label         "Show Requests"
    ::input-key     :dinsro.model.nostr.requests/id
    ::model-key     :dinsro.model.nostr.requests/id
    ::parent-key    :nostr-requests
    ::route         :dinsro.ui.nostr.requests/ShowPage
    ::router        :nostr
    ::required-role :user}

   :nostr-requests-show-connections
   {::control       :dinsro.ui.nostr.requests.connections/SubPage
    ::label         "Connections"
    ::model-key     :dinsro.model.nostr.connections/id
    ::parent-key    :nostr-requests-show
    ::route         :dinsro.ui.nostr.requests.connections/SubPage
    ::router        :nostr-requests
    ::required-role :user}

   :nostr-requests-show-filters
   {::control       :dinsro.ui.nostr.requests.filters/SubPage
    ::label         "Filters"
    ::model-key     :dinsro.model.nostr.filters/id
    ::parent-key    :nostr-requests-show
    ::route         :dinsro.ui.nostr.requests.filters/SubPage
    ::router        :nostr-requests
    ::required-role :user}

   :nostr-requests-show-items
   {::control       :dinsro.ui.nostr.requests.filter-items/SubPage
    ::label         "Items"
    ::model-key     :dinsro.model.nostr.filter-items/id
    ::parent-key    :nostr-requests-show
    ::route         :dinsro.ui.nostr.requests.filter-items/SubPage
    ::router        :nostr-requests
    ::required-role :user}

   :nostr-requests-show-runs
   {::control       :dinsro.ui.nostr.requests.runs/SubPage
    ::label         "Runs"
    ::model-key     :dinsro.model.nostr.runs/id
    ::parent-key    :nostr-requests-show
    ::route         :dinsro.ui.nostr.requests.runs/SubPage
    ::router        :nostr-requests
    ::required-role :user}

   :nostr-runs-show-witnesses
   {::control       :dinsro.ui.nostr.runs.witnesses/SubPage
    ::label         "Witnesses"
    ::model-key     :dinsro.model.nostr.witnesses/id
    ::parent-key    :nostr-runs-show
    ::route         :dinsro.ui.nostr.runs.witnesses/SubPage
    ::router        :nostr-runs
    ::required-role :user}

   :nostr-subscriptions-show-pubkeys
   {::control       :dinsro.ui.nostr.subscription-pubkeys/SubPage
    ::label         "Pubkeys"
    ::model-key     :dinsro.model.nostr.subscription-pubkeys/id
    ::parent-key    :nostr-subscriptions-show
    ::route         :dinsro.ui.nostr.subscription-pubkeys/SubPage
    ::router        :nostr
    ::required-role :user}

   :registration
   {::control       :dinsro.ui.registration/Page
    ::label         "Registration"
    ::parent-key    :root
    ::route         :dinsro.ui.registration/Page
    ::router        :root
    ::required-role :user}

   :root
   {::control       :dinsro.ui.home/Page
    ::label         "Home"
    ::parent-key    nil
    ::route         :dinsro.ui.home/Page
    ::required-role :guest}

   :settings
   {::control       :dinsro.ui.settings.dashboard/Page
    ::label         "Settings"
    ::parent-key    :root
    ::route         :dinsro.ui.settings.dashboard/Page
    ::router        :root
    ::required-role :user}

   :settings-categories
   {::control       :dinsro.ui.settings.categories/IndexPage
    ::label         "Categories"
    ::model-key     :dinsro.model.categories/id
    ::parent-key    :settings
    ::route         :dinsro.ui.settings.categories/IndexPage
    ::router        :settings
    ::required-role :user}

   :settings-categories-show
   {::control       :dinsro.ui.settings.categories/IndexPage
    ::label         "Show Category"
    ::input-key     :dinsro.model.categories/id
    ::model-key     :dinsro.model.categories/id
    ::parent-key    :settings-categories
    ::route         :dinsro.ui.settings.categories/IndexPage
    ::router        :settings
    ::required-role :user}

   :settings-core
   {::control       :dinsro.ui.settings.core.dashboards/Page
    ::label         "Core"
    ::parent-key    :settings
    ::route         :dinsro.ui.settings.core.dashboards/Page
    ::router        :settings
    ::required-role :user}

   :settings-core-dashboard
   {::control       :dinsro.ui.settings.core.dashboards/Page
    ::label         "Dashboard"
    ::parent-key    :settings-core
    ::route         :dinsro.ui.settings.core.dashboards/Page
    ::router        :settings
    ::required-role :user}

   :settings-dashboard
   {::control       :dinsro.ui.settings.dashboard/Page
    ::label         "Dashboard"
    ::parent-key    :setting
    ::route         :dinsro.ui.settings.dashboard/Page
    ::router        :settings
    ::required-role :user}

   :settings-ln
   {::control       :dinsro.ui.settings.ln.dashboard/Page
    ::label         "Lightning"
    ::model-key     :dinsro.model.ln.payments/id
    ::navigate-key  :settings-ln-dashboard
    ::parent-key    :settings
    ::route         :dinsro.ui.settings.ln.dashboard/Page
    ::router        :settings
    ::required-role :user}

   :settings-ln-channels
   {::control       :dinsro.ui.settings.ln.channels/IndexPage
    ::label         "Channels"
    ::model-key     :dinsro.model.ln.channels/id
    ::parent-key    :settings-ln
    ::route         :dinsro.ui.settings.ln.channels/IndexPage
    ::router        :settings-ln
    ::required-role :user}

   :settings-ln-dashboard
   {::control       :dinsro.ui.settings.ln.dashboard/Page
    ::label         "Dashboard"
    ::parent-key    :settings-ln
    ::route         :dinsro.ui.settings.ln.dashboard/Page
    ::router        :settings-ln
    ::required-role :user}

   :settings-ln-nodes
   {::control       :dinsro.ui.settings.ln.nodes/IndexPage
    ::label         "Nodes"
    ::model-key     :dinsro.model.ln.nodes/id
    ::parent-key    :settings-ln
    ::route         :dinsro.ui.settings.ln.nodes/IndexPage
    ::router        :settings-ln
    ::required-role :user}

   :settings-ln-nodes-show-accounts
   {::control       :dinsro.ui.ln.nodes.accounts/SubPage
    ::label         "Accounts"
    ::model-key     :dinsro.model.accounts/id
    ::parent-key    :settings-ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.accounts/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-nodes-show-addresses
   {::control       :dinsro.ui.ln.nodes.addresses/SubPage
    ::label         "Addresses"
    ::model-key     :dinsro.model.ln.addresses/id
    ::parent-key    :settings-ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.addresses/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-nodes-show-channels
   {::control       :dinsro.ui.ln.nodes.channels/SubPage
    ::label         "Channels"
    ::model-key     :dinsro.model.ln.channels/id
    ::parent-key    :settings-ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.channels/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-nodes-show-peers
   {::control       :dinsro.ui.ln.nodes.peers/SubPage
    ::label         "Peers"
    ::model-key     :dinsro.model.ln.peers/id
    ::parent-key    :settings-ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.peers/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-nodes-show-remote-nodes
   {::control       :dinsro.ui.ln.nodes.remote-nodes/SubPage
    ::label         "Remote Nodes"
    ::model-key     :dinsro.model.ln.remote-noders/id
    ::parent-key    :settings-ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.remote-nodes/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-nodes-show-wallet-addresses
   {::control       :dinsro.ui.ln.nodes.wallet-addresses/SubPage
    ::label         "Wallet Addresses"
    ::model-key     :dinsro.model.ln.wallet-addresses/id
    ::parent-key    :settings-ln-nodes-show
    ::route         :dinsro.ui.ln.nodes.wallet-addresses/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-payments
   {::control       :dinsro.ui.settings.ln.payments/IndexPage
    ::label         "Payments"
    ::model-key     :dinsro.model.ln.payments/id
    ::parent-key    :settings-ln
    ::route         :dinsro.ui.settings.ln.payments/IndexPage
    ::router        :settings-ln
    ::required-role :user}

   :settings-ln-payreqs
   {::control       :dinsro.ui.settings.ln.payreqs/IndexPage
    ::label         "Payreqs"
    ::model-key     :dinsro.model.ln.payreqs/id
    ::parent-key    :settings-ln
    ::route         :dinsro.ui.settings.ln.payreqs/IndexPage
    ::router        :settings-ln
    ::required-role :user}

   :settings-ln-remote-nodes
   {::control       :dinsro.ui.settings.ln.remote-nodes/IndexPage
    ::label         "Remote Nodes"
    ::model-key     :dinsro.model.ln.remote-nodes/id
    ::parent-key    :settings-ln
    ::route         :dinsro.ui.settings.ln.remote-nodes/IndexPage
    ::router        :settings-ln
    ::required-role :user}

   :settings-rate-sources
   {::control       :dinsro.ui.settings.rate-sources/IndexPage
    ::label         "Rate Sources"
    ::model-key     :dinsro.model.rate-sources/id
    ::parent-key    :settings
    ::route         :dinsro.ui.settings.rate-sources/IndexPage
    ::router        :settings
    ::required-role :user}

   :settings-rate-sources-show
   {::control       :dinsro.ui.settings.rate-sources/IndexPage
    ::label         "Show Rate Sources"
    ::input-key     :dinsro.model.rate-sources/id
    ::model-key     :dinsro.model.rate-sources/id
    ::parent-key    :settings-rate-sources
    ::route         :dinsro.ui.settings.rate-sources/IndexPage
    ::router        :settings
    ::required-role :user}

   :settings-rate-sources-show-accounts
   {::control       :dinsro.ui.settings.rate-sources.accounts/SubPage
    ::label         "Accounts"
    ::input-key     :dinsro.model.rate-sources/id
    ::model-key     :dinsro.model.accounts/id
    ::parent-key    :settings-rate-sources-show
    ::route         :dinsro.ui.settings.rate-sources.accounts/SubPage
    ::router        :settings-rate-sources
    ::required-role :user}

   :transactions
   {::control       :dinsro.ui.transactions/IndexPage
    ::label         "Transactions"
    ::model-key     :dinsro.model.transactions/id
    ::parent-key    :root
    ::route         :dinsro.ui.transactions/IndexPage
    ::router        :root
    ::required-role :user}

   :transactions-show
   {::control       :dinsro.ui.transactions/ShowPage
    ::label         "Show Transaction"
    ::input-key     :dinsro.model.transactions/id
    ::model-key     :dinsro.model.transactions/id
    ::parent-key    :transactions
    ::route         :dinsro.ui.transactions/ShowPage
    ::router        :root
    ::required-role :user}})

(s/def ::id keyword?)
(s/def ::label string?)

(defn ident [id] {::id id})
(defn idents [ids] (mapv ident ids))

(defn serve-route-key
  ([key]
   (serve-route-key key false nil))
  ([key use-replacement? replacement]
   (fn [_env props]
     (if-let [id (get props ::id)]
       (if-let [value (get-in routes [id key])]
         {key value}
         (if use-replacement?
           {key replacement}
           (throw (ex-info "No value found" {:key key :props props}))))
       (throw (ex-info "No id found" {:key key :props props}))))))

(defn find-nav-target
  [id]
  id)

(comment

  (find-nav-target :admin)

  nil)

(defattr id ::id :keyword
  {ao/identity? true
   ao/pc-resolve (fn [_env {::keys [id] :as props}]
                   (if-let [_record (get routes id)]
                     {::id id}
                     (do
                       (log/error :id/not-found {:id id})
                       (throw (ex-info "No id found" {:props props})))))})

;; if true, this link starts the login process

(s/def ::auth-link? boolean?)
(defattr auth-link? ::auth-link? :boolean
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::auth-link? true false)})

;; A keyword naming the control

(s/def ::control keyword?)
(defattr control ::control :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::control)})

;; A string describing the route

(s/def ::description string?)
(defattr description ::description :string
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::description true "")})

;; The model key identifying this route's target

(s/def ::input-key (s/or :nil nil? :keyword keyword?))
(defattr input-key ::input-key :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::input-key true nil)})

;; The string to show on the menu

(s/def ::label string?)
(defattr label ::label :string?
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::label)})

;; The model key that this route returns

(s/def ::model-key (s/or :nil nil? :keyword keyword?))
(defattr model-key ::model-key :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::model-key true nil)})

;; A reference to a navlink that navigating to this link should navigate to the other instead

;; (s/def ::navigate (s/or :nil nil? :keyword keyword?))
(defattr navigate ::navigate :ref
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve
   (fn [_env props]
     (let [{::keys [id]} props
           nav-id       (find-nav-target id)]
       {::navigate (ident nav-id)}))})

;; A key naming a navlink that navigating to this link should navigate to the other instead

(s/def ::navigate-key (s/or :nil nil? :keyword keyword?))
(defattr navigate-key ::navigate-key :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::navigate-key true nil)})

;; A reference to this route's parent

(defattr parent ::parent :ref
  {ao/identities #{::id}
   ao/pc-input   #{::id ::parent-key}
   ao/pc-resolve (fn [_env {:keys [parent-key]}]
                   {::parent (ident parent-key)})})

;; A keyword naming this route's parent

(defattr parent-key ::parent-key :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::parent-key true nil)})

;; The minimum required role to access this route

(defattr required-role ::required-role :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::required-role)})

;; "A keyword naming the page component's navigation target"

(defattr route ::route :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::route)})

;; "The id of this link's parent navbar. While multiple bars can point at a link, this is the bar that this link considers its parent"
(defattr router ::router :ref
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [{::router [:dinsro.model.navbars/id]}]
   ao/target     :dinsro.model.navbars/id
   ao/pc-resolve (fn [_env props]
                   (log/info :router/starting {:props props})
                   (let [{::keys [id]} props
                         router        (get-in routes [id ::router])]
                     {::router (when router {:dinsro.model.navbars/id router})}))})

;; the record identified by the supplied id

(defattr target ::target :ref
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [{::target [:dinsro.model.users/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :target/starting {:props props :query-params query-params})
     (let [{::keys [id]} props]
       (if-let [navlink (get routes id)]
         (if-let [model-key (get-in routes [id ::model-key])]
           (if-let [record-id (get query-params model-key)]
             (do
               (log/info :target/targeted {:navlink navlink :record-id record-id})
               {::target {model-key record-id}})
             (throw (ex-info "Failed to find record" {:id id})))
           (throw (ex-info "Failed to find model key" {:id id})))
         (throw (ex-info "Failed to find navlink" {:id id})))))})

(def attributes
  [id
   auth-link?
   control
   description
   input-key
   label
   model-key
   navigate
   navigate-key
   parent
   parent-key
   required-role
   route
   router
   target])
