(ns dinsro.model
  (:require
   [com.fulcrologic.rad.attributes :as attr]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.joins.categories :as j.categories]
   [dinsro.joins.core.addresses :as j.core-addresses]
   [dinsro.joins.core.blocks :as j.core-blocks]
   [dinsro.joins.core.nodes :as j.core-nodes]
   [dinsro.joins.core.peers :as j.core-peers]
   [dinsro.joins.core.tx :as j.core-tx]
   [dinsro.joins.core.tx-in :as j.core-tx-in]
   [dinsro.joins.core.tx-out :as j.core-tx-out]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.joins.ln.channels :as j.ln-channels]
   [dinsro.joins.ln.invoices :as j.ln-invoices]
   [dinsro.joins.ln.nodes :as j.ln-nodes]
   [dinsro.joins.ln.payments :as j.ln-payments]
   [dinsro.joins.ln.payreqs :as j.ln-payreqs]
   [dinsro.joins.ln.peers :as j.ln-peers]
   [dinsro.joins.ln.tx :as j.ln-tx]
   [dinsro.joins.rates :as j.rates]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.joins.users :as j.users]
   [dinsro.joins.core.wallet-addresses :as j.wallet-addresses]
   [dinsro.joins.core.wallets :as j.wallets]
   [dinsro.joins.core.words :as j.words]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.core.addresses :as m.core-addresses]
   [dinsro.model.core.blocks :as m.core-blocks]
   [dinsro.model.core.nodes :as m.core-nodes]
   [dinsro.model.core.peers :as m.core-peers]
   [dinsro.model.core.script-sigs :as m.core-script-sig]
   [dinsro.model.core.tx :as m.core-tx]
   [dinsro.model.core.tx-in :as m.core-tx-in]
   [dinsro.model.core.tx-out :as m.core-tx-out]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.ln.chains :as m.ln-chain]
   [dinsro.model.ln.channels :as m.ln-channels]
   [dinsro.model.ln.nodes :as m.ln-nodes]
   [dinsro.model.ln.info :as m.ln-info]
   [dinsro.model.ln.invoices :as m.ln-invoices]
   [dinsro.model.ln.payments :as m.ln-payments]
   [dinsro.model.ln.payreqs :as m.ln-payreqs]
   [dinsro.model.ln.peers :as m.ln-peers]
   [dinsro.model.ln.remote-nodes :as m.ln-remote-nodes]
   [dinsro.model.ln.transactions :as m.ln-tx]
   [dinsro.model.navbar :as m.navbar]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.settings :as m.settings]
   [dinsro.model.timezone :as m.timezone]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.model.core.wallets :as m.wallets]
   [dinsro.model.core.wallet-addresses :as m.wallet-addresses]
   [dinsro.model.core.words :as m.words]
   #?(:clj [dinsro.mutations.accounts :as mu.accounts])
   #?(:clj [dinsro.mutations.core.addresses :as mu.core-addresses])
   #?(:clj [dinsro.mutations.core.blocks :as mu.core-blocks])
   #?(:clj [dinsro.mutations.core.nodes :as mu.core-nodes])
   #?(:clj [dinsro.mutations.core.peers :as mu.core-peers])
   #?(:clj [dinsro.mutations.core.tx :as mu.core-tx])
   #?(:clj [dinsro.mutations.ln.invoices :as mu.ln-invoices])
   #?(:clj [dinsro.mutations.ln.nodes :as mu.ln-nodes])
   #?(:clj [dinsro.mutations.ln.payreqs :as mu.ln-payreqs])
   #?(:clj [dinsro.mutations.rate-sources :as mu.rate-sources])
   #?(:clj [dinsro.mutations.session :as mu.session])
   #?(:clj [dinsro.mutations.settings :as mu.settings])
   #?(:clj [dinsro.mutations.core.wallets :as mu.wallets])
   #?(:clj [dinsro.mutations.core.wallet-addresses :as mu.wallet-addresses])
   #?(:clj [dinsro.mutations.core.words :as mu.words])))

(def schemata [])

(def all-attributes
  (vec (concat
        j.accounts/attributes
        j.categories/attributes
        j.core-addresses/attributes
        j.core-blocks/attributes
        j.core-nodes/attributes
        j.core-peers/attributes
        j.core-tx/attributes
        j.core-tx-in/attributes
        j.core-tx-out/attributes
        j.currencies/attributes
        j.ln-channels/attributes
        j.ln-invoices/attributes
        j.ln-nodes/attributes
        j.ln-payments/attributes
        j.ln-payreqs/attributes
        j.ln-peers/attributes
        j.ln-tx/attributes
        j.rates/attributes
        j.rate-sources/attributes
        j.transactions/attributes
        j.users/attributes
        j.wallet-addresses/attributes
        j.wallets/attributes
        j.words/attributes
        m.accounts/attributes
        m.categories/attributes
        m.core-addresses/attributes
        m.core-blocks/attributes
        m.core-nodes/attributes
        m.core-peers/attributes
        m.core-script-sig/attributes
        m.core-tx/attributes
        m.core-tx-in/attributes
        m.core-tx-out/attributes
        m.currencies/attributes
        m.ln-chain/attributes
        m.ln-channels/attributes
        m.ln-info/attributes
        m.ln-invoices/attributes
        m.ln-nodes/attributes
        m.ln-payments/attributes
        m.ln-payreqs/attributes
        m.ln-peers/attributes
        m.ln-remote-nodes/attributes
        m.ln-tx/attributes
        m.navbar/attributes
        m.navlink/attributes
        m.rates/attributes
        m.rate-sources/attributes
        m.settings/attributes
        m.timezone/attributes
        m.transactions/attributes
        m.users/attributes
        m.wallets/attributes
        m.wallet-addresses/attributes
        m.words/attributes)))

(def all-attribute-validator (attr/make-attribute-validator all-attributes))

#?(:clj
   (def all-resolvers
     (vec (concat
           m.navlink/resolvers
           mu.accounts/resolvers
           mu.core-addresses/resolvers
           mu.core-blocks/resolvers
           mu.core-nodes/resolvers
           mu.core-peers/resolvers
           mu.core-tx/resolvers
           mu.ln-invoices/resolvers
           mu.ln-payreqs/resolvers
           mu.ln-nodes/resolvers
           mu.rate-sources/resolvers
           mu.session/resolvers
           mu.settings/resolvers
           mu.wallets/resolvers
           mu.wallet-addresses/resolvers
           mu.words/resolvers))))
