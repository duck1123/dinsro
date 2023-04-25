(ns dinsro.model
  (:require
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.joins.categories :as j.categories]
   [dinsro.joins.contacts :as j.contacts]
   [dinsro.joins.core.addresses :as j.c.addresses]
   [dinsro.joins.core.blocks :as j.c.blocks]
   [dinsro.joins.core.chains :as j.c.chains]
   [dinsro.joins.core.connections :as j.c.connections]
   [dinsro.joins.core.networks :as j.c.networks]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.joins.core.peers :as j.c.peers]
   [dinsro.joins.core.script-sigs :as j.c.script-sigs]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.joins.core.tx-in :as j.c.tx-in]
   [dinsro.joins.core.tx-out :as j.c.tx-out]
   [dinsro.joins.core.wallet-addresses :as j.c.wallet-addresses]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.joins.core.words :as j.c.words]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.joins.debits :as j.debits]
   [dinsro.joins.ln.accounts :as j.ln.accounts]
   [dinsro.joins.ln.channels :as j.ln.channels]
   [dinsro.joins.ln.invoices :as j.ln.invoices]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.joins.ln.payments :as j.ln.payments]
   [dinsro.joins.ln.payreqs :as j.ln.payreqs]
   [dinsro.joins.ln.peers :as j.ln.peers]
   [dinsro.joins.ln.remote-nodes :as j.ln.remote-nodes]
   [dinsro.joins.nostr.badge-acceptances :as j.n.badge-acceptances]
   [dinsro.joins.nostr.badge-awards :as j.n.badge-awards]
   [dinsro.joins.nostr.badge-definitions :as j.n.badge-definitions]
   [dinsro.joins.nostr.connections :as j.n.connections]
   [dinsro.joins.nostr.event-tags :as j.n.event-tags]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.joins.nostr.filter-items :as j.n.filter-items]
   [dinsro.joins.nostr.filters :as j.n.filters]
   [dinsro.joins.nostr.pubkey-contacts :as j.n.pubkey-contacts]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.joins.nostr.requests :as j.n.requests]
   [dinsro.joins.nostr.runs :as j.n.runs]
   [dinsro.joins.nostr.subscription-pubkeys :as j.n.subscription-pubkeys]
   [dinsro.joins.nostr.subscriptions :as j.n.subscriptions]
   [dinsro.joins.nostr.witnesses :as j.n.witnesses]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.joins.rates :as j.rates]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.joins.users :as j.users]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.connections :as m.c.connections]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.core.script-sigs :as m.c.script-sigs]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-in :as m.c.transactions-in]
   [dinsro.model.core.tx-out :as m.c.transactions-out]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.model.navbar :as m.navbar]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.contact-relays :as m.n.contact-relays]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.pubkey-contacts :as m.n.pubkey-contacts]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.settings :as m.settings]
   [dinsro.model.timezone :as m.timezone]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.user-pubkeys :as m.user-pubkeys]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.mutations.accounts :as mu.accounts])
   #?(:clj [dinsro.mutations.contacts :as mu.contacts])
   #?(:clj [dinsro.mutations.core.addresses :as mu.c.addresses])
   #?(:clj [dinsro.mutations.core.blocks :as mu.c.blocks])
   #?(:clj [dinsro.mutations.core.connections :as mu.c.connections])
   #?(:clj [dinsro.mutations.core.nodes :as mu.c.nodes])
   #?(:clj [dinsro.mutations.core.peers :as mu.c.peers])
   #?(:clj [dinsro.mutations.core.transactions :as mu.c.transactions])
   #?(:clj [dinsro.mutations.core.wallets :as mu.c.wallets])
   #?(:clj [dinsro.mutations.core.wallet-addresses :as mu.c.wallet-addresses])
   #?(:clj [dinsro.mutations.core.words :as mu.c.words])
   #?(:clj [dinsro.mutations.currencies :as mu.currencies])
   #?(:clj [dinsro.mutations.debits :as mu.debits])
   #?(:clj [dinsro.mutations.ln.accounts :as mu.ln.accounts])
   #?(:clj [dinsro.mutations.ln.invoices :as mu.ln.invoices])
   #?(:clj [dinsro.mutations.ln.nodes :as mu.ln.nodes])
   #?(:clj [dinsro.mutations.ln.payments :as mu.ln.payments])
   #?(:clj [dinsro.mutations.ln.payreqs :as mu.ln.payreqs])
   #?(:clj [dinsro.mutations.ln.peers :as mu.ln.peers])
   #?(:clj [dinsro.mutations.ln.remote-nodes :as mu.ln.remote-nodes])
   #?(:clj [dinsro.mutations.nostr.badge-acceptances :as mu.n.badge-acceptances])
   #?(:clj [dinsro.mutations.nostr.badge-awards :as mu.n.badge-awards])
   #?(:clj [dinsro.mutations.nostr.badge-definitions :as mu.n.badge-definitons])
   #?(:clj [dinsro.mutations.nostr.connections :as mu.n.connections])
   #?(:clj [dinsro.mutations.nostr.event-tags :as mu.n.event-tags])
   #?(:clj [dinsro.mutations.nostr.events :as mu.n.events])
   #?(:clj [dinsro.mutations.nostr.filter-items :as mu.n.filter-items])
   #?(:clj [dinsro.mutations.nostr.filters :as mu.n.filters])
   #?(:clj [dinsro.mutations.nostr.pubkey-contacts :as mu.n.pubkey-contacts])
   #?(:clj [dinsro.mutations.nostr.pubkey-events :as mu.n.pubkey-events])
   #?(:clj [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys])
   #?(:clj [dinsro.mutations.nostr.relays :as mu.n.relays])
   #?(:clj [dinsro.mutations.nostr.requests :as mu.n.requests])
   #?(:clj [dinsro.mutations.nostr.runs :as mu.n.runs])
   #?(:clj [dinsro.mutations.nostr.subscription-pubkeys :as mu.n.subscription-pubkeys])
   #?(:clj [dinsro.mutations.nostr.subscriptions :as mu.n.subscriptions])
   #?(:clj [dinsro.mutations.nostr.witnesses :as mu.n.witnesses])
   #?(:clj [dinsro.mutations.rate-sources :as mu.rate-sources])
   #?(:clj [dinsro.mutations.session :as mu.session])
   #?(:clj [dinsro.mutations.settings :as mu.settings])
   #?(:clj [dinsro.mutations.users :as mu.users])))

(def all-attributes
  (vec (concat
        j.accounts/attributes
        j.categories/attributes
        j.contacts/attributes
        j.c.addresses/attributes
        j.c.blocks/attributes
        j.c.chains/attributes
        j.c.connections/attributes
        j.c.networks/attributes
        j.c.nodes/attributes
        j.c.peers/attributes
        j.c.script-sigs/attributes
        j.c.transactions/attributes
        j.c.tx-in/attributes
        j.c.tx-out/attributes
        j.c.wallet-addresses/attributes
        j.c.wallets/attributes
        j.c.words/attributes
        j.currencies/attributes
        j.debits/attributes
        j.ln.accounts/attributes
        j.ln.channels/attributes
        j.ln.invoices/attributes
        j.ln.nodes/attributes
        j.ln.payments/attributes
        j.ln.payreqs/attributes
        j.ln.peers/attributes
        j.ln.remote-nodes/attributes
        j.n.badge-acceptances/attributes
        j.n.badge-awards/attributes
        j.n.badge-definitions/attributes
        j.n.connections/attributes
        j.n.event-tags/attributes
        j.n.events/attributes
        j.n.filter-items/attributes
        j.n.filters/attributes
        j.n.pubkey-contacts/attributes
        j.n.pubkeys/attributes
        j.n.relays/attributes
        j.n.requests/attributes
        j.n.runs/attributes
        j.n.subscription-pubkeys/attributes
        j.n.subscriptions/attributes
        j.n.witnesses/attributes
        j.rates/attributes
        j.rate-sources/attributes
        j.transactions/attributes
        j.users/attributes
        m.accounts/attributes
        m.categories/attributes
        m.contacts/attributes
        m.c.addresses/attributes
        m.c.blocks/attributes
        m.c.chains/attributes
        m.c.connections/attributes
        m.c.mnemonics/attributes
        m.c.networks/attributes
        m.c.nodes/attributes
        m.c.peers/attributes
        m.c.script-sigs/attributes
        m.c.transactions/attributes
        m.c.transactions-in/attributes
        m.c.transactions-out/attributes
        m.c.wallets/attributes
        m.c.wallet-addresses/attributes
        m.c.words/attributes
        m.currencies/attributes
        m.debits/attributes
        m.ln.accounts/attributes
        m.ln.channels/attributes
        m.ln.info/attributes
        m.ln.invoices/attributes
        m.ln.nodes/attributes
        m.ln.payments/attributes
        m.ln.payreqs/attributes
        m.ln.peers/attributes
        m.ln.remote-nodes/attributes
        m.n.badge-acceptances/attributes
        m.n.badge-awards/attributes
        m.n.badge-definitions/attributes
        m.n.connections/attributes
        m.n.contact-relays/attributes
        m.n.event-tags/attributes
        m.n.events/attributes
        m.n.filter-items/attributes
        m.n.filters/attributes
        m.n.pubkey-contacts/attributes
        m.n.pubkeys/attributes
        m.n.relays/attributes
        m.n.requests/attributes
        m.n.runs/attributes
        m.n.subscription-pubkeys/attributes
        m.n.subscriptions/attributes
        m.n.witnesses/attributes
        m.navbar/attributes
        m.navlink/attributes
        m.rates/attributes
        m.rate-sources/attributes
        m.settings/attributes
        m.timezone/attributes
        m.transactions/attributes
        m.user-pubkeys/attributes
        m.users/attributes)))

#?(:clj
   (def all-resolvers
     (vec (concat
           m.navlink/resolvers
           mu.accounts/resolvers
           mu.contacts/resolvers
           mu.c.addresses/resolvers
           mu.c.connections/resolvers
           mu.c.blocks/resolvers
           mu.c.nodes/resolvers
           mu.c.peers/resolvers
           mu.c.transactions/resolvers
           mu.c.wallets/resolvers
           mu.c.wallet-addresses/resolvers
           mu.c.words/resolvers
           mu.currencies/resolvers
           mu.debits/resolvers
           mu.ln.accounts/resolvers
           mu.ln.invoices/resolvers
           mu.ln.payments/resolvers
           mu.ln.payreqs/resolvers
           mu.ln.peers/resolvers
           mu.ln.nodes/resolvers
           mu.ln.remote-nodes/resolvers
           mu.n.badge-acceptances/resolvers
           mu.n.badge-awards/resolvers
           mu.n.badge-definitons/resolvers
           mu.n.connections/resolvers
           mu.n.event-tags/resolvers
           mu.n.events/resolvers
           mu.n.filter-items/resolvers
           mu.n.filters/resolvers
           mu.n.pubkey-contacts/resolvers
           mu.n.pubkey-events/resolvers
           mu.n.pubkeys/resolvers
           mu.n.relays/resolvers
           mu.n.requests/resolvers
           mu.n.runs/resolvers
           mu.n.subscription-pubkeys/resolvers
           mu.n.subscriptions/resolvers
           mu.n.witnesses/resolvers
           mu.rate-sources/resolvers
           mu.session/resolvers
           mu.settings/resolvers
           mu.users/resolvers))))
