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
    ::route         :dinsro.ui.accounts/IndexPage
    ::router        :root
    ::required-role :user}

   :accounts-show
   {::label         "Show Accounts"
    ::description   "Show page for an account"
    ::control       :dinsro.ui.accounts/ShowPage
    ::input-key     :dinsro.model.accounts/id
    ::model-key     :dinsro.model.accounts/id
    ::route         :dinsro.ui.accounts/ShowPage
    ::router        :root
    ::required-role :user}

   :admin
   {::label         "Admin"
    ::description   "Admin root page"
    ::control       :dinsro.ui.admin/Page
    ::navigate-key  :admin-users
    ::route         :dinsro.ui.admin.users/IndexPage
    ::router        :root
    ::required-role :admin}

   :admin-accounts
   {::label         "Accounts"
    ::description   "Admin page of all accounts"
    ::control       :dinsro.ui.admin.accounts/IndexPage
    ::model-key     :dinsro.model.accounts/id
    ::route         :dinsro.ui.admin.accounts/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-accounts-show
   {::label         "Accounts"
    ::description   "Admin page for account"
    ::control       :dinsro.ui.admin.accounts/ShowPage
    ::input-key     :dinsro.model.accounts/id
    ::model-key     :dinsro.model.accounts/id
    ::route         :dinsro.ui.admin.accounts/ShowPage
    ::router        :admin
    ::required-role :admin}

   :admin-categories
   {::label         "Categories"
    ::description   "Admin page of all categories"
    ::constrol      :dinsro.ui.admin.categories/IndexPage
    ::model-key     :dinsro.model.accounts/categories
    ::route         :dinsro.ui.admin.categories/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-categories-show
   {::label         "Categories"
    ::description   "Admin page for category"
    ::control       :dinsro.ui.admin.categories/ShowPage
    ::input-key     :dinsro.model.accounts/categories
    ::model-key     :dinsro.model.accounts/categories
    ::route         :dinsro.ui.admin.categories/ShowPage
    ::router        :admin
    ::required-role :admin}

   :admin-core
   {::label         "Core"
    ::description   "Router page for core admin"
    ::control       :dinsro.ui.admin.core/Page
    ::route         :dinsro.ui.admin.core.dashboard/Page
    ::router        :admin
    ::required-role :admin}

   :admin-core-addresses
   {::label         "Addresses"
    ::description   "Admin index for core addresses"
    ::control       :dinsro.ui.admin.core.addresses/IndexPage
    ::model-key     :dinsro.model.core.addresses/id
    ::route         :dinsro.ui.admin.core.addresses/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-addresses-show
   {::label         "Show Address"
    ::description   "Admin core address"
    ::control       :dinsro.ui.admin.core.addresses/ShowPage
    ::input-key     :dinsro.model.core.addresses/id
    ::model-key     :dinsro.model.core.addresses/id
    ::route         :dinsro.ui.admin.core.addresses/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-blocks
   {::label         "Blocks"
    ::description   "Admin index blocks"
    ::control       :dinsro.ui.admin.core.blocks/IndexPage
    ::input-key     :dinsro.model.core.blocks/id
    ::model-key     :dinsro.model.core.blocks/id
    ::route         :dinsro.ui.admin.core.blocks/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-blocks-show
   {::label         "Show Block"
    ::description   "Admin show block"
    ::control       :dinsro.ui.admin.core.blocks/ShowPage
    ::input-key     :dinsro.model.core.blocks/id
    ::model-key     :dinsro.model.core.blocks/id
    ::route         :dinsro.ui.admin.core.blocks/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-chains
   {::label         "Chains"
    ::description   "Admin index chains"
    ::control       :dinsro.ui.admin.core.chains/IndexPage
    ::model-key     :dinsro.model.core.chains/id
    ::route         :dinsro.ui.admin.core.chains/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-chains-show
   {::label         "Show Chain"
    ::description   "Admin show chain"
    ::control       :dinsro.ui.admin.core.chains/ShowPage
    ::input-key     :dinsro.model.core.chains/id
    ::model-key     :dinsro.model.core.chains/id
    ::route         :dinsro.ui.admin.core.chains/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-dashboard
   {::label         "Dashboard"
    ::description   "Admin Core Dashboard"
    ::control       :dinsro.ui.admin.core.dashboard/Page
    ::route         :dinsro.ui.admin.core.dashboard/Page
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-mnemonics
   {::label         "Mnemonics"
    ::description   "Admin Index Mnemonics"
    ::control       :dinsro.ui.admin.core.mnemonics/IndexPage
    ::model-key     :dinsro.model.core.mnemonics/id
    ::route         :dinsro.ui.admin.core.mnemonics/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-mnemonics-show
   {::label         "Mnemonics"
    ::description   "Admin show mnemonic"
    ::control       :dinsro.ui.admin.core.mnemonics/ShowPage
    ::input-key     :dinsro.model.core.mnemonics/id
    ::model-key     :dinsro.model.core.mnemonics/id
    ::route         :dinsro.ui.admin.core.mnemonics/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-networks
   {::label         "Networks"
    ::description   "Admin index networks"
    ::control       :dinsro.ui.admin.core.networks/IndexPage
    ::model-key     :dinsro.model.core.networks/id
    ::route         :dinsro.ui.admin.core.networks/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-networks-show
   {::label         "Show Network"
    ::description   "Admin Show Network"
    ::control       :dinsro.ui.admin.core.networks/ShowPage
    ::input-key     :dinsro.model.core.networks/id
    ::model-key     :dinsro.model.core.networks/id
    ::route         :dinsro.ui.admin.core.networks/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-nodes
   {::label         "Nodes"
    ::description   "Admin Index Core Nodes"
    ::model-key     :dinsro.model.core.nodes/id
    ::route         :dinsro.ui.admin.core.nodes/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-nodes-show
   {::label         "Show Node"
    ::model-key     :dinsro.model.core.nodes/id
    ::route         :dinsro.ui.admin.core.nodes/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-peers
   {::label         "Peers"
    ::model-key     :dinsro.model.core.peers/id
    ::route         :dinsro.ui.admin.core.peers/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-peers-show
   {::label         "Show Peer"
    ::model-key     :dinsro.model.core.peers/id
    ::route         :dinsro.ui.admin.core.peers/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-transactions
   {::label         "Transactions"
    ::model-key     :dinsro.model.core.transactions/id
    ::route         :dinsro.ui.admin.core.transactions/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-transactions-show
   {::label         "Show Transaction"
    ::model-key     :dinsro.model.core.transactions/id
    ::route         :dinsro.ui.admin.core.transactions/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-wallets
   {::label         "Wallets"
    ::model-key     :dinsro.model.core.wallets/id
    ::route         :dinsro.ui.admin.core.wallets/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-wallets-show
   {::label         "Show Wallet"
    ::model-key     :dinsro.model.core.wallets/id
    ::route         :dinsro.ui.admin.core.wallets/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-words
   {::label         "Words"
    ::model-key     :dinsro.model.core.words/id
    ::route         :dinsro.ui.admin.core.words/IndexPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-core-words-show
   {::label         "Show Word"
    ::input-key     :dinsro.model.core.words/id
    ::model-key     :dinsro.model.core.words/id
    ::route         :dinsro.ui.admin.core.words/ShowPage
    ::router        :admin-core
    ::required-role :admin}

   :admin-currencies
   {::label         "Currencies"
    ::model-key     :dinsro.model.currencies/id
    ::route         :dinsro.ui.admin.currencies/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-currencies-show
   {::label         "Show Currency"
    ::model-key     :dinsro.model.currencies/id
    ::route         :dinsro.ui.admin.currencies/ShowPage
    ::router        :admin
    ::required-role :admin}

   :admin-debits
   {::label         "Debits"
    ::model-key     :dinsro.model.debits/id
    ::route         :dinsro.ui.admin.debits/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-debits-show
   {::label         "Show Debit"
    ::model-key     :dinsro.model.debits/id
    ::route         :dinsro.ui.admin.debits/ShowPage
    ::router        :admin
    ::required-role :admin}

   :admin-ln
   {::label         "LN"
    ::route         :dinsro.ui.admin.ln/Page
    ;; ::menu           :admin-ln
    ::router        :admin
    ::required-role :admin}

   :admin-ln-accounts
   {::label         "Accounts"
    ::model-key     :dinsro.model.ln.accounts/id
    ::route         :dinsro.ui.admin.ln.accounts/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-accounts-show
   {::label         "Show Account"
    ::model-key     :dinsro.model.ln.accounts/id
    ::route         :dinsro.ui.admin.ln.accounts/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-channels
   {::label         "Channels"
    ::model-key     :dinsro.model.ln.channels/id
    ::route         :dinsro.ui.admin.ln.channels/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-channels-show
   {::label         "Show Channel"
    ::model-key     :dinsro.model.ln.channels/id
    ::route         :dinsro.ui.admin.ln.channels/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-dashboard
   {::label         "Dashboard"
    ::route         :dinsro.ui.admin.ln/Page
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-invoices
   {::label     "Invoices"
    ::model-key :dinsro.model.ln.invoices/id
    ::route     :dinsro.ui.admin.ln.invoices/IndexPage
    ::router    :admin-ln}

   :admin-ln-invoices-show
   {::label         "Show Invoice"
    ::model-key     :dinsro.model.ln.invoices/id
    ::route         :dinsro.ui.admin.ln.invoices/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-nodes
   {::label         "Nodes"
    ::model-key     :dinsro.model.ln.nodes/id
    ::route         :dinsro.ui.admin.ln.nodes/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-nodes-show
   {::label         "Show Node"
    ::model-key     :dinsro.model.ln.nodes/id
    ::route         :dinsro.ui.admin.ln.nodes/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-payments
   {::label         "Payments"
    ::model-key     :dinsro.model.ln.payments/id
    ::route         :dinsro.ui.admin.ln.payments/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-payments-show
   {::label         "Show Payment"
    ::model-key     :dinsro.model.ln.payments/id
    ::route         :dinsro.ui.admin.ln.payments/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-payreqs
   {::label         "Payment Requests"
    ::model-key     :dinsro.model.ln.payreqs/id
    ::route         :dinsro.ui.admin.ln.payreqs/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-payreqs-show
   {::label         "Show Payment Request"
    ::model-key     :dinsro.model.ln.payreqs/id
    ::route         :dinsro.ui.admin.ln.payreqs/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-peers
   {::label         "Peers"
    ::model-key     :dinsro.model.ln.peers/id
    ::route         :dinsro.ui.admin.ln.peers/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-peers-show
   {::label         "Peers"
    ::model-key     :dinsro.model.ln.peers/id
    ::route         :dinsro.ui.admin.ln.peers/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-remote-nodes
   {::label         "Remote Nodes"
    ::model-key     :dinsro.model.ln.remote-nodes/id
    ::route         :dinsro.ui.admin.ln.remote-nodes/IndexPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-ln-remote-nodes-show
   {::label         "Show Remote Node"
    ::model-key     :dinsro.model.ln.remote-nodes/id
    ::route         :dinsro.ui.admin.ln.remote-nodes/ShowPage
    ::router        :admin-ln
    ::required-role :admin}

   :admin-models
   {::label         "Models"
    ::route         :dinsro.ui.admin.models/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-nostr
   {::label         "Nostr"
    ::route         :dinsro.ui.admin.nostr.dashboard/Page
    ;; ::menu           :admin-nostr
    ::router        :admin
    ::required-role :admin}

   :admin-nostr-badge-acceptances
   {::label         "Acceptances"
    ::model-key     :dinsro.model.nostr.badge-acceptances/id
    ::route         :dinsro.ui.admin.nostr.badge-acceptances/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-badge-acceptances-show
   {::label         "Acceptances"
    ::model-key     :dinsro.model.nostr.badge-acceptances/id
    ::route         :dinsro.ui.admin.nostr.badge-acceptances/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-badge-awards
   {::label         "Awards"
    ::model-key     :dinsro.model.nostr.badge-awards/id
    ::route         :dinsro.ui.admin.nostr.badge-awards/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-badge-awards-show
   {::label         "Awards"
    ::model-key     :dinsro.model.nostr.badge-awards/id
    ::route         :dinsro.ui.admin.nostr.badge-awards/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-badge-definitions
   {::label         "Definitions"
    ::model-key     :dinsro.model.nostr.badge-definitions/id
    ::route         :dinsro.ui.admin.nostr.badge-definitions/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-badge-definitions-show
   {::label         "Definitions"
    ::model-key     :dinsro.model.nostr.badge-definitions/id
    ::route         :dinsro.ui.admin.nostr.badge-definitions/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-connections
   {::label         "Connections"
    ::model-key     :dinsro.model.nostr.connections/id
    ::route         :dinsro.ui.admin.nostr.connections/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-connections-show
   {::label         "Connections"
    ::model-key     :dinsro.model.nostr.connections/id
    ::route         :dinsro.ui.admin.nostr.connections/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-connections-runs
   {::label         "Runs"
    ::model-key     :dinsro.model.nostr.runs/id
    ::route         :dinsro.ui.nostr.connections.runs/SubPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-dashboard
   {::label         "dashboard"
    ::route         :dinsro.ui.admin.nostr.dashboard/Page
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-events
   {::label         "Events"
    ::description   "Admin index of events"
    ::model-key     :dinsro.model.nostr.events/id
    ::route         :dinsro.ui.admin.nostr.events/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-events-show
   {::label          "Show Event"
    ::description    "Admin page for an event"
    ::model-key      :dinsro.model.nostr.events/id
    ::route          :dinsro.ui.admin.nostr.events/ShowPage
    ::router         :admin-nostr
    ::required-role  :admin
    ::requires-auth? true}

   :admin-nostr-filter-items
   {::label          "Items"
    ::model-key      :dinsro.model.nostr.filter-items/id
    ::route          :dinsro.ui.admin.nostr.filter-items/IndexPage
    ::router         :admin-nostr
    ::required-role  :admin
    ::requires-auth? true}

   :admin-nostr-filter-items-show
   {::label          "Items"
    ::model-key      :dinsro.model.nostr.filter-items/id
    ::route          :dinsro.ui.admin.nostr.filter-items/ShowPage
    ::router         :admin-nostr
    ::required-role  :admin
    ::requires-auth? true}

   :admin-nostr-filters
   {::label          "Filters"
    ::model-key      :dinsro.model.nostr.filters/id
    ::route          :dinsro.ui.admin.nostr.filters/IndexPage
    ::router         :admin-nostr
    ::required-role  :admin
    ::requires-auth? true}

   :admin-nostr-filters-show
   {::label          "Show Filter"
    ::model-key      :dinsro.model.nostr.filters/id
    ::route          :dinsro.ui.admin.nostr.filters/ShowPage
    ::router         :admin-nostr
    ::required-role  :admin
    ::requires-auth? true}

   :admin-nostr-pubkeys
   {::label          "Pubkeys"
    ::model-key      :dinsro.model.nostr.pubkeys/id
    ::route          :dinsro.ui.admin.nostr.pubkeys/IndexPage
    ::router         :admin-nostr
    ::required-role  :admin
    ::requires-auth? true}

   :admin-nostr-pubkeys-show
   {::label          "Show Pubkey"
    ::input-key      :dinsro.model.nostr.pubkeys/id
    ::model-key      :dinsro.model.nostr.pubkeys/id
    ::route          :dinsro.ui.admin.nostr.pubkeys/ShowPage
    ::router         :admin-nostr
    ::required-role  :admin
    ::requires-auth? true}

   :admin-nostr-relays
   {::label          "Relays"
    ::model-key      :dinsro.model.nostr.relays/id
    ::route          :dinsro.ui.admin.nostr.relays/IndexPage
    ::router         :admin-nostr
    ::required-role  :admin
    ::requires-auth? true}

   :admin-nostr-relays-show
   {::label          "Show Relay"
    ::input-key      :dinsro.model.nostr.relays/id
    ::model-key      :dinsro.model.nostr.relays/id
    ::route          :dinsro.ui.admin.nostr.relays/ShowPage
    ::router         :admin-nostr
    ::required-role  :admin
    ::requires-auth? true}

   :admin-nostr-relays-connections
   {::label          "Connections"
    ::input-key      :dinsro.model.nostr.relays/id
    ::model-key      :dinsro.model.nostr.connections/id
    ::route          :dinsro.ui.admin.nostr.relays.connections/SubPage
    ::router         :admin-nostr-relays
    ::required-role  :admin
    ::requires-auth? true}

   :admin-nostr-relays-events
   {::label          "Events"
    ::input-key      :dinsro.model.nostr.relays/id
    ::model-key      :dinsro.model.nostr.events/id
    ::route          :dinsro.ui.admin.nostr.relays.events/SubPage
    ::router         :admin-nostr-relays
    ::required-role  :admin
    ::requires-auth? true}

   :admin-nostr-relays-pubkeys
   {::label         "Pubkeys"
    ::input-key     :dinsro.model.nostr.relays/id
    ::model-key     :dinsro.model.nostr.pubkeys/id
    ::route         :dinsro.ui.admin.nostr.relays.pubkeys/SubPage
    ::router        :admin-nostr-relays
    ::required-role :admin}

   :admin-nostr-relays-requests
   {::label         "Requests"
    ::input-key     :dinsro.model.nostr.relays/id
    ::model-key     :dinsro.model.nostr.requests/id
    ::route         :dinsro.ui.admin.nostr.relays.requests/SubPage
    ::router        :admin-nostr-relays
    ::required-role :admin}

   :admin-nostr-relays-runs
   {::label         "Runs"
    ::input-key     :dinsro.model.nostr.relays/id
    ::model-key     :dinsro.model.nostr.runs/id
    ::route         :dinsro.ui.admin.nostr.relays.runs/SubPage
    ::router        :admin-nostr-relays
    ::required-role :admin}

   :admin-nostr-relays-witnesses
   {::label         "Witnesses"
    ::input-key     :dinsro.model.nostr.relays/id
    ::model-key     :dinsro.model.nostr.witnesses/id
    ::route         :dinsro.ui.admin.nostr.relays.witnesses/SubPage
    ::router        :admin-nostr-relays
    ::required-role :admin}

   :admin-nostr-requests
   {::label         "Requests"
    ::model-key     :dinsro.model.nostr.requests/id
    ::route         :dinsro.ui.admin.nostr.requests/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-requests-connections
   {::label         "Connections"
    ::input-key     :dinsro.model.nostr.requests/id
    ::model-key     :dinsro.model.nostr.connections/id
    ::route         :dinsro.ui.admin.nostr.requests.connections/SubPage
    ::router        :admin-nostr-requests
    ::required-role :admin}

   :admin-nostr-requests-filters
   {::label         "Filters"
    ::input-key     :dinsro.model.nostr.requests/id
    ::model-key     :dinsro.model.nostr.filters/id
    ::route         :dinsro.ui.admin.nostr.requests.filters/SubPage
    ::router        :admin-nostr-requests
    ::required-role :admin}

   :admin-nostr-requests-items
   {::label         "Items"
    ::input-key     :dinsro.model.nostr.requests/id
    ::model-key     :dinsro.model.nostr.filter-items/id
    ::route         :dinsro.ui.admin.nostr.requests.filter-items/SubPage
    ::required-role :admin}

   :admin-nostr-requests-runs
   {::label         "Runs"
    ::input-key     :dinsro.model.nostr.requests/id
    ::model-key     :dinsro.model.nostr.runs/id
    ::route         :dinsro.ui.admin.nostr.requests.runs/SubPage
    ::router        :admin-nostr-requests
    ::required-role :admin}

   :admin-nostr-requests-show
   {::label         "Show Request"
    ::input-key     :dinsro.model.nostr.requests/id
    ::model-key     :dinsro.model.nostr.requests/id
    ::route         :dinsro.ui.admin.nostr.requests/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-runs
   {::label         "Runs"
    ::model-key     :dinsro.model.nostr.runs/id
    ::route         :dinsro.ui.admin.nostr.runs/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-witnesses
   {::label         "Witnesses"
    ::model-key     :dinsro.model.nostr.runs/id
    ::route         :dinsro.ui.admin.nostr.witnesses/IndexPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-nostr-witnesses-show
   {::label         "Show Witness"
    ::input-key     :dinsro.model.nostr.witnesses/id
    ::model-key     :dinsro.model.nostr.witnesses/id
    ::route         :dinsro.ui.admin.nostr.witnesses/ShowPage
    ::router        :admin-nostr
    ::required-role :admin}

   :admin-rate-sources
   {::label         "Rate Sources"
    ::model-key     :dinsro.model.rate-sources/id
    ::route         :dinsro.ui.admin.rate-sources/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-rates
   {::label         "Rates"
    ::model-key     :dinsro.model.rates/id
    ::route         :dinsro.ui.admin.rates/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-transactions
   {::label         "Transactions"
    ::model-key     :dinsro.model.transactions/id
    ::route         :dinsro.ui.admin.transactions/IndexPage
    ::router        :admin
    ::required-role :admin}

   :admin-users
   {::label          "Users"
    ::model-key      :dinsro.model.users/id
    ::route          :dinsro.ui.admin.users/IndexPage
    ::router         :admin
    ::required-role  :admin
    ::requires-auth? true}

   :admin-users-accounts
   {::label          "Accounts"
    ::model-key      :dinsro.model.accounts/id
    ::route          :dinsro.ui.admin.users.accounts/SubPage
    ::router         :admin-users
    ::required-role  :admin
    ::requires-auth? true}

   :admin-users-categories
   {::label          "Categories"
    ::model-key      :dinsro.model.categories/id
    ::route          :dinsro.ui.admin.users.categories/SubPage
    ::router         :admin-users
    ::required-role  :admin
    ::requires-auth? true}

   :admin-users-debits
   {::label          "Debits"
    ::model-key      :dinsro.model.debits/id
    ::route          :dinsro.ui.admin.users.debits/SubPage
    ::router         :admin-users
    ::required-role  :admin
    ::requires-auth? true}

   :admin-users-ln-nodes
   {::label          "LN Nodes"
    ::model-key      :dinsro.model.ln.nodes/id
    ::route          :dinsro.ui.admin.users.ln-nodes/SubPage
    ::router         :admin-users
    ::required-role  :admin
    ::requires-auth? true}

   :admin-users-pubkeys
   {::label          "Pubkeys"
    ::model-key      :dinsro.model.nostr.pubkeys/id
    ::route          :dinsro.ui.admin.users.pubkeys/SubPage
    ::router         :admin-users
    ::required-role  :admin
    ::requires-auth? true}

   :admin-users-show
   {::label          "Admin Show User"
    ::input-key      :dinsro.model.users/id
    ::model-key      :dinsro.model.users/id
    ::route          :dinsro.ui.admin.users/ShowPage
    ::router         :admin
    ::requires-auth? true
    ::required-role  :admin
    ::target         :dinsro.model.users/id}

   :admin-users-transactions
   {::label          "Transactions"
    ::model-key      :dinsro.model.transactions/id
    ::route          :dinsro.ui.admin.users.transactions/SubPage
    ::router         :admin-users
    ::required-role  :admin
    ::requires-auth? true}

   :admin-users-user-pubkeys
   {::label          "User Pubkeys"
    ::model-key      :dinsro.model.nostr.pubkeys/id
    ::route          :dinsro.ui.admin.users.user-pubkeys/SubPage
    ::router         :admin-users
    ::required-role  :admin
    ::requires-auth? true}

   :admin-users-wallets
   {::label          "Wallets"
    ::model-key      :dinsro.model.core.wallets/id
    ::route          :dinsro.ui.admin.users.wallets/SubPage
    ::router         :admin-users
    ::required-role  :admin
    ::requires-auth? true}

   :contacts
   {::label          "Contacts"
    ::model-key      :dinsro.model.contacts/id
    ::route          :dinsro.ui.contacts/IndexPage
    ::router         :root
    ::required-role  :user
    ::requires-auth? true}

   :core
   {::label         "Core"
    ::description   "Router for core"
    ::control       :dinsro.ui.core/Page
    ::route         :dinsro.ui.core.dashboard/Page
    ::router        :route
    ::required-role :guest}

   :core-blocks-show
   {::label         "Show Block"
    ::input-key     :dinsro.model.core.blocks/id
    ::model-key     :dinsro.model.core.blocks/id
    ::route         :dinsro.ui.core.blocks/ShowPage
    ::router        :core
    ::required-role :user}

   :core-chains-networks
   {::label         "Networks"
    ::model-key     :dinsro.model.core.networks/id
    ::route         :dinsro.ui.core.chain-networks/SubPage
    ::router        :core
    ::required-role :user}

   :core-chains-show
   {::label         "Show Chain"
    ::model-key     :dinsro.model.core.chains/id
    ::route         :dinsro.ui.core.chains/ShowPage
    ::router        :core
    ::required-role :user}

   :core-networks-addresses
   {::label         "Addresses"
    ::model-key     :dinsro.model.core.addresses/id
    ::route         :dinsro.ui.core.networks.addresses/SubPage
    ::router        :core-networks
    ::required-role :user}

   :core-networks-blocks
   {::label         "Blocks"
    ::model-key     :dinsro.model.core.blocks/id
    ::route         :dinsro.ui.core.networks.blocks/SubPage
    ::router        :core-networks
    ::required-role :user}

   :core-networks-core-nodes
   {::label         "Core Nodes"
    ::model-key     :dinsro.model.core.nodes/id
    ::route         :dinsro.ui.core.networks.nodes/SubPage
    ::router        :core-networks
    ::required-role :user}

   :core-networks-ln-nodes
   {::label         "LN Nodes"
    ::model-key     :dinsro.model.ln.nodes/id
    ::route         :dinsro.ui.core.networks.ln-nodes/SubPage
    ::router        :core-networks
    ::required-role :user}

   :core-networks-show
   {::label         "Show Network"
    ::input-key     :dinsro.model.core.networks/id
    ::model-key     :dinsro.model.core.networks/id
    ::route         :dinsro.ui.core.networks/ShowPage
    ::router        :core
    ::required-role :user}

   :core-networks-wallets
   {::label         "Wallets"
    ::model-key     :dinsro.model.core.wallets/id
    ::route         :dinsro.ui.core.networks.wallets/SubPage
    ::router        :core-networks
    ::required-role :user}

   :core-nodes-blocks
   {::label         "Blocks"
    ::model-key     :dinsro.model.core.blocks/id
    ::route         :dinsro.ui.core.nodes.blocks/SubPage
    ::router        :core-nodes
    ::required-role :user}

   :core-nodes-peers
   {::label         "Peers"
    ::model-key     :dinsro.model.core.peers/id
    ::route         :dinsro.ui.core.nodes.peers/SubPage
    ::router        :core-nodes
    ::required-role :user}

   :core-nodes-show
   {::label         "Show Node"
    ::input-key     :dinsro.model.core.nodes/id
    ::model-key     :dinsro.model.core.nodes/id
    ::route         :dinsro.ui.core.nodes/ShowPage
    ::router        :core
    ::required-role :user}

   :core-transactions-show
   {::label         "Show Transaction"
    ::input-key     :dinsro.model.core.transactions/id
    ::model-key     :dinsro.model.core.transactions/id
    ::route         :dinsro.ui.core.transactions/ShowPage
    ::router        :core
    ::required-role :user}

   :core-wallets-show
   {::label         "Show Wallet"
    ::input-key     :dinsro.model.core.wallets/id
    ::model-key     :dinsro.model.core.wallets/id
    ::route         :dinsro.ui.core.wallets/ShowPage
    ::router        :core
    ::required-role :user}

   :currencies
   {::label         "Currencies"
    ::model-key     :dinsro.model.currencies/id
    ::route         :dinsro.ui.currencies/IndexPage
    ::router        :root
    ::required-role :user}

   :currencies-accounts
   {::label         "Accounts"
    ::input-key     :dinsro.model.currencies/id
    ::model-key     :dinsro.model.accounts/id
    ::route         :dinsro.ui.currencies.accounts/SubPage
    ::router        :currencies
    ::required-role :user}

   :currencies-rates
   {::label         "Rates"
    ::input-key     :dinsro.model.currencies/id
    ::model-key     :dinsro.model.rates/id
    ::route         :dinsro.ui.currencies.rates/SubPage
    ::router        :currencies
    ::required-role :user}

   :currencies-rate-sources
   {::label         "Rates Sources"
    ::input-key     :dinsro.model.currencies/id
    ::model-key     :dinsro.model.rate-sources/id
    ::route         :dinsro.ui.currencies.rate-sources/SubPage
    ::router        :currencies
    ::required-role :user}

   :currencies-show
   {::label         "Show Currency"
    ::input-key     :dinsro.model.currencies/id
    ::model-key     :dinsro.model.currencies/id
    ::route         :dinsro.ui.currencies/ShowPage
    ::router        :root
    ::required-role :user}

   :home
   {::label         "Home"
    ::route         :dinsro.ui.home/Page
    ::router        :root
    ::required-role :guest}

   :ln
   {::label         "LN Router"
    ;; ::model-key     :dinsro.model.lnnodes/id
    ::route         :dinsro.ui.ln/Page
    ::router        :root
    ::required-role :user}

   :ln-nodes
   {::label         "Index LN Nodes"
    ::model-key     :dinsro.model.ln.nodes/id
    ::route         :dinsro.ui.ln.nodes/ShowPage
    ::router        :ln
    ::required-role :user}

   :ln-nodes-accounts
   {::label         "Accounts"
    ::model-key     :dinsro.model.ln.accounts/id
    ::route         :dinsro.ui.ln.nodes.accounts/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-addresses
   {::label         "Addresses"
    ::model-key     :dinsro.model.ln.addresses/id
    ::route         :dinsro.ui.ln.nodes.addresses/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-channels
   {::label         "Channels"
    ::model-key     :dinsro.model.ln.channels/id
    ::route         :dinsro.ui.ln.nodes.channels/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-peers
   {::label         "Peers"
    ::model-key     :dinsro.model.ln.peers/id
    ::route         :dinsro.ui.ln.nodes.peers/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-remote-nodes
   {::label         "Remote Nodes"
    ::model-key     :dinsro.model.ln.remote-nodes/id
    ::route         :dinsro.ui.ln.nodes.remote-nodes/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-remote-nodes-show
   {::label         "Remote Nodes"
    ::input-key     :dinsro.model.ln.remote-nodes/id
    ::model-key     :dinsro.model.ln.remote-nodes/id
    ::route         :dinsro.ui.ln.nodes.remote-nodes/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-show
   {::label         "Show Node"
    ::model-key     :dinsro.model.ln.nodes/id
    ::route         :dinsro.ui.ln.nodes/ShowPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-nodes-wallet-addresses
   {::label         "Wallet Addresses"
    ::model-key     :dinsro.model.ln.wallet-addresses/id
    ::route         :dinsro.ui.ln.nodes.wallet-addresses/SubPage
    ::router        :ln-nodes
    ::required-role :user}

   :ln-remote-nodes-show
   {::label         "Show Remote Node"
    ::input-key     :dinsro.model.ln.remote-nodes/id
    ::model-key     :dinsro.model.ln.remote-nodes/id
    ::route         :dinsro.ui.ln.remote-nodes/ShowPage
    ::router        :ln
    ::required-role :user}

   :login
   {::auth-link?    true
    ::label         "Login"
    ::route         :dinsro.ui.login/Page
    ::required-role :guest
    ::router        :root}

   :navbars
   {::label          "Navbars"
    ::model-key      :dinsro.model.navbars/id
    ::route          :dinsro.ui.navbars/IndexPage
    ::router         :root
    ::required-role  :user
    ::requires-auth? true}

   :navlinks
   {::label          "Navlinks"
    ::model-key      ::id
    ::route          :dinsro.ui.navlinks/IndexPage
    ::router         :root
    ::required-role  :user
    ::requires-auth? true}

   :nodes
   {::label         "Nodes"
    ::route         :dinsro.ui.nodes/Page
    ::required-role :user
    ::router        :root}

   :nostr
   {::label         "Nostr"
    ::model-key     :dinsro.model.nostr.events/id
    ::route         :dinsro.ui.nostr.events/IndexPage
    ::required-role :user
    ::router        :root}

   :nostr-connections
   {::label          "Connections"
    ::model-key      :dinsro.model.nostr.connections/id
    ::route          :dinsro.ui.nostr.connections.runs/SubPage
    ::router         :nostr
    ::required-role  :user
    ::requires-auth? true}

   :nostr-connections-runs
   {::label          "Runs"
    ::model-key      :dinsro.model.nostr.runs/id
    ::route          :dinsro.ui.nostr.connections.runs/SubPage
    ::router         :nostr-connections
    ::required-role  :user
    ::requires-auth? true}

   :nostr-connections-show
   {::label          "Show Connection"
    ::input-key      :dinsro.model.nostr.connections/id
    ::model-key      :dinsro.model.nostr.connections/id
    ::route          :dinsro.ui.nostr.connections/ShowPage
    ::router         :nostr
    ::required-role  :user
    ::requires-auth? true}

   :nostr-event-tags-relays
   {::label          "Relays"
    ::model-key      :dinsro.model.nostr.relays/id
    ::route          :dinsro.ui.nostr.event-tags.relays/SubPage
    ::router         :nostr-event-tags
    ::required-role  :user
    ::requires-auth? true}

   :nostr-events
   {::label         "Events"
    ::model-key     :dinsro.model.nostr.events/id
    ::route         :dinsro.ui.nostr.events/IndexPage
    ::router        :nostr
    ::required-role :user}

   :nostr-events-relays
   {::label          "Relays"
    ::model-key      :dinsro.model.nostr.relays/id
    ::route          :dinsro.ui.nostr.events.relays/SubPage
    ::router         :nostr-events
    ::required-role  :user
    ::requires-auth? true}

   :nostr-events-tags
   {::label         "Tags"
    ::model-key     :dinsro.model.nostr.event-tags/id
    ::route         :dinsro.ui.nostr.events.event-tags/SubPage
    ::router        :nostr-events
    ::required-role :user}

   :nostr-events-witnesses
   {::label          "Witnesses"
    ::model-key      :dinsro.model.nostr.witnesses/id
    ::route          :dinsro.ui.nostr.events.witnesses/SubPage
    ::router         :nostr-events
    ::required-role  :user
    ::requires-auth? true}

   :nostr-filters-items
   {::label          "Items"
    ::model-key      :dinsro.model.nostr.filter-items/id
    ::route          :dinsro.ui.nostr.filters.filter-items/SubPage
    ::router         :nostr-filters
    ::required-role  :user
    ::requires-auth? true}

   :nostr-pubkeys-events
   {::label          "Events"
    ::model-key      :dinsro.model.nostr.events/id
    ::route          :dinsro.ui.nostr.pubkeys.events/SubPage
    ::router         :nostr-pubkeys
    ::required-role  :user
    ::requires-auth? true}

   :nostr-pubkeys-items
   {::label         "Filter Items"
    ::model-key     :dinsro.model.nostr.items/id
    ::route         :dinsro.ui.nostr.pubkeys.items/SubPage
    ::router        :nostr-pubkeys
    ::required-role :user}

   :nostr-pubkeys-relays
   {::label          "Relays"
    ::model-key      :dinsro.model.nostr.relays/id
    ::route          :dinsro.ui.nostr.pubkeys.relays/SubPage
    ::router         :nostr-pubkeys
    ::requires-auth? true
    ::required-role  :user}

   :nostr-pubkeys-show
   {::label          "Show Pubkey"
    ::input-key      :dinsro.model.nostr.pubkeys/id
    ::model-key      :dinsro.model.nostr.pubkeys/id
    ::route          :dinsro.ui.nostr.pubkeys/ShowPage
    ::router         :nostr
    ::required-role  :user
    ::requires-auth? true}

   :registration
   {::label         "Registration"
    ::route         :dinsro.ui.registration/Page
    ::router        :root
    ::required-role :user}

   :nostr-relays-connections
   {::label          "Connections"
    ::model-key      :dinsro.model.nostr.connections/id
    ::route          :dinsro.ui.nostr.relays.connections/SubPage
    ::router         :nostr-relays
    ::required-role  :user
    ::requires-auth? true}

   :nostr-relays-events
   {::label          "Events"
    ::model-key      :dinsro.model.nostr.events/id
    ::route          :dinsro.ui.nostr.relays.events/SubPage
    ::router         :nostr-relays
    ::required-role  :user
    ::requires-auth? true}

   :nostr-relays-pubkeys
   {::label          "Pubkeys"
    ::model-key      :dinsro.model.nostr.pubkeys/id
    ::route          :dinsro.ui.nostr.relays.pubkeys/SubPage
    ::router         :nostr-relays
    ::required-role  :user
    ::requires-auth? true}

   :nostr-relays-requests
   {::label          "Requests"
    ::model-key      :dinsro.model.nostr.requests/id
    ::route          :dinsro.ui.nostr.relays.requests/SubPage
    ::router         :nostr-relays
    ::required-role  :user
    ::requires-auth? true}

   :nostr-relays-runs
   {::label          "Runs"
    ::model-key      :dinsro.model.nostr.runs/id
    ::route          :dinsro.ui.nostr.relays.runs/SubPage
    ::router         :nostr-relays
    ::required-role  :user
    ::requires-auth? true}

   :nostr-relays-show
   {::label          "Show Relay"
    ::input-key      :dinsro.model.nostr.relays/id
    ::model-key      :dinsro.model.nostr.relays/id
    ::route          :dinsro.ui.nostr.relays/ShowPage
    ::router         :nostr
    ::required-role  :user
    ::requires-auth? true}

   :nostr-relays-witnesses
   {::label          "Witnesses"
    ::model-key      :dinsro.model.nostr.witnesses/id
    ::route          :dinsro.ui.nostr.relays.witnesses/SubPage
    ::router         :nostr-relays
    ::required-role  :user
    ::requires-auth? true}

   :nostr-requests-connections
   {::label          "Connections"
    ::model-key      :dinsro.model.nostr.connections/id
    ::route          :dinsro.ui.nostr.requests.connections/SubPage
    ::router         :nostr-requests
    ::required-role  :user
    ::requires-auth? true}

   :nostr-requests-filters
   {::label          "Filters"
    ::model-key      :dinsro.model.nostr.filters/id
    ::route          :dinsro.ui.nostr.requests.filters/SubPage
    ::router         :nostr-requests
    ::required-role  :user
    ::requires-auth? true}

   :nostr-requests-items
   {::label         "Items"
    ::model-key     :dinsro.model.nostr.filter-items/id
    ::route         :dinsro.ui.nostr.requests.filter-items/SubPage
    ::router        :nostr-requests
    ::required-role :user}

   :nostr-requests-runs
   {::label         "Runs"
    ::model-key     :dinsro.model.nostr.runs/id
    ::route         :dinsro.ui.nostr.requests.runs/SubPage
    ::router        :nostr-requests
    ::required-role :user}

   :nostr-requests-show
   {::label         "Show Requests"
    ::input-key     :dinsro.model.nostr.requests/id
    ::model-key     :dinsro.model.nostr.requests/id
    ::route         :dinsro.ui.nostr.requests/ShowPage
    ::router        :nostr
    ::required-role :user}

   :nostr-runs-witnesses
   {::label         "Witnesses"
    ::model-key     :dinsro.model.nostr.witnesses/id
    ::route         :dinsro.ui.nostr.runs.witnesses/SubPage
    ::router        :nostr-runs
    ::required-role :user}

   :nostr-subscriptions-pubkeys
   {::label         "Pubkeys"
    ::model-key     :dinsro.model.nostr.subscription-pubkeys/id
    ::route         :dinsro.ui.nostr.subscription-pubkeys/SubPage
    ::router        :nostr
    ::required-role :user}

   :root
   {::label         "Home"
    ::route         :dinsro.ui.home/Page
    ::required-role :guest}

   :settings
   {::label         "Settings"
    ::route         :dinsro.ui.settings.dashboard/Page
    ::router        :root
    ::required-role :user}

   :settings-categories
   {::label         "Categories"
    ::model-key     :dinsro.model.categories/id
    ::route         :dinsro.ui.settings.categories/IndexPage
    ::router        :settings
    ::required-role :user}

   :settings-categories-show
   {::label         "Show Category"
    ::input-key     :dinsro.model.categories/id
    ::model-key     :dinsro.model.categories/id
    ::route         :dinsro.ui.settings.categories/IndexPage
    ::router        :settings
    ::required-role :user}

   :settings-core
   {::label         "Core"
    ::route         :dinsro.ui.settings.core.dashboards/Page
    ::router        :settings
    ::required-role :user}

   :settings-core-dashboard
   {::label         "Dashboard"
    ::route         :dinsro.ui.settings.core.dashboards/Page
    ::router        :settings
    ::required-role :user}

   :settings-dashboard
   {::label         "Dashboard"
    ::route         :dinsro.ui.settings.dashboard/Page
    ::router        :settings
    ::required-role :user}

   :settings-ln
   {::label         "Lightning"
    ::model-key     :dinsro.model.ln.payments/id
    ::route         :dinsro.ui.settings.ln.dashboard/Page
    ::router        :settings
    ::required-role :user}

   :settings-ln-channels
   {::label         "Channels"
    ::model-key     :dinsro.model.ln.channels/id
    ::route         :dinsro.ui.settings.ln.channels/IndexPage
    ::router        :settings-ln
    ::required-role :user}

   :settings-ln-dashboard
   {::label         "Dashboard"
    ::route         :dinsro.ui.settings.ln.dashboard/Page
    ::router        :settings-ln
    ::required-role :user}

   :settings-ln-nodes
   {::label         "Nodes"
    ::model-key     :dinsro.model.ln.nodes/id
    ::route         :dinsro.ui.settings.ln.nodes/IndexPage
    ::router        :settings-ln
    ::required-role :user}

   :settings-ln-nodes-accounts
   {::label         "Accounts"
    ::model-key     :dinsro.model.accounts/id
    ::route         :dinsro.ui.ln.nodes.accounts/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-nodes-addresses
   {::label         "Addresses"
    ::model-key     :dinsro.model.ln.addresses/id
    ::route         :dinsro.ui.ln.nodes.addresses/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-nodes-channels
   {::label         "Channels"
    ::model-key     :dinsro.model.ln.channels/id
    ::route         :dinsro.ui.ln.nodes.channels/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-nodes-peers
   {::label         "Peers"
    ::model-key     :dinsro.model.ln.peers/id
    ::route         :dinsro.ui.ln.nodes.peers/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-nodes-remote-nodes
   {::label         "Remote Nodes"
    ::model-key     :dinsro.model.ln.remote-noders/id
    ::route         :dinsro.ui.ln.nodes.remote-nodes/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-nodes-wallet-addresses
   {::label         "Wallet Addresses"
    ::model-key     :dinsro.model.ln.wallet-addresses/id
    ::route         :dinsro.ui.ln.nodes.wallet-addresses/SubPage
    ::router        :settings-ln-nodes
    ::required-role :user}

   :settings-ln-payments
   {::label         "Payments"
    ::model-key     :dinsro.model.ln.payments/id
    ::route         :dinsro.ui.settings.ln.payments/IndexPage
    ::router        :settings-ln
    ::required-role :user}

   :settings-ln-payreqs
   {::label         "Payreqs"
    ::model-key     :dinsro.model.ln.payreqs/id
    ::route         :dinsro.ui.settings.ln.payreqs/IndexPage
    ::router        :settings-ln
    ::required-role :user}

   :settings-ln-remote-nodes
   {::label         "Remote Nodes"
    ::model-key     :dinsro.model.ln.remote-nodes/id
    ::route         :dinsro.ui.settings.ln.remote-nodes/IndexPage
    ::router        :settings-ln
    ::required-role :user}

   :settings-rate-sources
   {::label         "Rate Sources"
    ::model-key     :dinsro.model.rate-sources/id
    ::route         :dinsro.ui.settings.rate-sources/IndexPage
    ::router        :settings
    ::required-role :user}

   :settings-rate-sources-accounts
   {::label         "Accounts"
    ::input-key     :dinsro.model.rate-sources/id
    ::model-key     :dinsro.model.accounts/id
    ::route         :dinsro.ui.settings.rate-sources.accounts/SubPage
    ::router        :settings-rate-sources
    ::required-role :user}

   :settings-rate-sources-show
   {::label         "Show Rate Sources"
    ::input-key     :dinsro.model.rate-sources/id
    ::model-key     :dinsro.model.rate-sources/id
    ::route         :dinsro.ui.settings.rate-sources/IndexPage
    ::router        :settings
    ::required-role :user}

   :transactions
   {::label         "Transactions"
    ::model-key     :dinsro.model.transactions/id
    ::route         :dinsro.ui.transactions/IndexPage
    ::router        :root
    ::required-role :user}

   :transactions-show
   {::label         "Show Transaction"
    ::input-key     :dinsro.model.transactions/id
    ::model-key     :dinsro.model.transactions/id
    ::route         :dinsro.ui.transactions/ShowPage
    ::router        :root
    ::required-role :user}})

(s/def ::id keyword?)
(s/def ::label string?)

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

(defattr id ::id :keyword
  {ao/identity? true})

;; if true, this link starts the login process
(defattr auth-link? ::auth-link? :boolean
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::auth-link? true false)})

(defattr description ::description :string?
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::description true "")})

(defattr input-key ::input-key :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::input-key true nil)})

(defattr label ::label :string?
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::label)})

;; The id of a navbar contained in this page
;; (defattr menu ::menu :keyword
;;   {ao/identities #{::id}
;;    ao/pc-input   #{::id}
;;    ao/pc-resolve (serve-route-key ::model-key)})

(defattr model-key ::model-key :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::model-key true nil)})

(defattr required-role ::required-role :keyword
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-resolve (serve-route-key ::required-role)})

;; (defattr requires-auth? ::requires-auth? :boolean
;;   {ao/identities #{::id}
;;    ao/pc-input   #{::id}
;;    ao/pc-resolve (serve-route-key ::requires-auth?)})

;; "A keyword naming the page component"
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

(defn ident [id] {::id id})
(defn idents [ids] (mapv ident ids))

(def attributes
  [id
   auth-link?
   description
   input-key
   label
   model-key
   required-role
   route
   router
   target])
