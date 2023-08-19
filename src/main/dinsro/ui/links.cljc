(ns dinsro.ui.links
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.routing :as rroute]
   [dinsro.joins.nostr.connections :as j.n.connections]
   [dinsro.joins.nostr.filters :as j.n.filters]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.joins.nostr.requests :as j.n.requests]
   [dinsro.joins.users :as j.users]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.instances :as m.instances]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [lambdaisland.glogc :as log]))

(defn form-link
  [this id name form-kw]
  (dom/a {:href      "#"
          :data-form (str form-kw)
          :onClick
          (fn [e]
            (.preventDefault e)
            (if-let [component (comp/registry-key->class form-kw)]
              (do
                (log/debug :form-link/clicked
                  {:id        id
                   :name      name
                   :form-kw   form-kw
                   :component component})
                (rroute/route-to! this component {:id (str id)}))
              (log/error :form-link/no-component {:form-kw form-kw})))}
    name))

(def refresh-control
  "config to add a refresh button to a report"
  {:type   :button
   :label  "Refresh"
   :action (fn [this] (control/run! this))})

(defn img-formatter
  [pubkey]
  (if-let [picture (::m.n.pubkeys/picture pubkey)]
    (dom/img {:src picture})
    ""))

(form/defsc-form AccountLinkForm
  [this {::m.accounts/keys [id name]}]
  {fo/id           m.accounts/id
   fo/route-prefix "account-link"
   fo/attributes   [m.accounts/name]}
  (form-link this id name :dinsro.ui.accounts/ShowPage))

(def ui-account-link
  "Display account as a link"
  (comp/factory AccountLinkForm {:keyfn ::m.accounts/id}))

(form/defsc-form AdminAccountLinkForm
  [this {::m.accounts/keys [id name]}]
  {fo/id           m.accounts/id
   fo/route-prefix "admin-account-link"
   fo/attributes   [m.accounts/name]}
  (form-link this id name :dinsro.ui.admin.accounts/ShowPage))

(def ui-admin-account-link
  "Display account as a link"
  (comp/factory AdminAccountLinkForm {:keyfn ::m.accounts/id}))

(form/defsc-form AdminAddressLinkForm
  [this {::m.c.addresses/keys [id address]}]
  {fo/id           m.c.addresses/id
   fo/route-prefix "admin-address-link"
   fo/attributes   [m.c.addresses/id m.c.addresses/address]}
  (form-link this id (str address) :dinsro.ui.admin.core.addresses/ShowPage))

(def ui-admin-address-link (comp/factory AdminAddressLinkForm {:keyfn ::m.c.addresses/id}))

(form/defsc-form AdminBlockLinkForm
  [this {::m.c.blocks/keys [id hash]}]
  {fo/id           m.c.blocks/id
   fo/route-prefix "admin-block-link"
   fo/attributes   [m.c.blocks/hash]}
  (log/info :BlockLinkForm/starting {:id id :hash hash})
  (form-link this id hash :dinsro.ui.admin.core.blocks/ShowPage))

(def ui-admin-block-link (comp/factory AdminBlockLinkForm {:keyfn ::m.c.blocks/id}))

(form/defsc-form AdminBlockHeightLinkForm
  [this {::m.c.blocks/keys [id height]}]
  {fo/id           m.c.blocks/id
   fo/route-prefix "admin-block-height-link"
   fo/attributes   [m.c.blocks/height]}
  (form-link this id height :dinsro.ui.admin.core.blocks/ShowPage))

(def ui-admin-block-height-link (comp/factory AdminBlockHeightLinkForm {:keyfn ::m.c.blocks/id}))

(form/defsc-form AdminCategoryLinkForm
  [this {::m.categories/keys [id name]}]
  {fo/id           m.categories/id
   fo/route-prefix "admin-category-link"
   fo/attributes   [m.categories/name]}
  (form-link this id name :dinsro.ui.admin.categories/ShowPage))

(def ui-admin-category-link (comp/factory AdminCategoryLinkForm {:keyfn ::m.categories/id}))

(form/defsc-form AdminChainLinkForm [this {::m.c.chains/keys [id name]}]
  {fo/id           m.c.chains/id
   fo/route-prefix "admin-chain-link"
   fo/attributes   [m.c.chains/name]}
  (form-link this id name :dinsro.ui.admin.core.chains/ShowPage))

(def ui-admin-chain-link (comp/factory AdminChainLinkForm {:keyfn ::m.c.chains/id}))

(form/defsc-form AdminConnectionLinkForm
  [this {::m.n.connections/keys [id status]}]
  {fo/id           m.n.connections/id
   fo/route-prefix "admin-nostr-connection-link"
   fo/attributes   [m.n.connections/id m.n.connections/status]}
  (form-link this id (name status) :dinsro.ui.admin.nostr.connections.runs/SubPage))

(def ui-admin-connection-link (comp/factory AdminConnectionLinkForm {:keyfn ::m.n.connections/id}))

(form/defsc-form AdminConnectionRunCountLinkForm
  [this {::m.n.connections/keys [id]
         ::j.n.connections/keys [run-count]}]
  {fo/id           m.n.connections/id
   fo/route-prefix "admin-nostr-connection-run-count-link"
   fo/attributes   [m.n.connections/id j.n.connections/run-count]}
  (form-link this id run-count :dinsro.ui.admin.nostr.connections.runs/SubPage))

(def ui-admin-connection-run-count-link
  (comp/factory AdminConnectionRunCountLinkForm {:keyfn ::m.n.connections/id}))

(form/defsc-form AdminCoreNodeLinkForm
  [this {::m.c.nodes/keys [id name] :as props}]
  {fo/id           m.c.nodes/id
   fo/route-prefix "admin-core-node-link"
   fo/attributes   [m.c.nodes/id m.c.nodes/name]}
  (log/trace :AdminCoreNodeLinkForm/starting {:id id :name name :props props})
  (form-link this id (or name (str id)) :dinsro.ui.admin.core.nodes/ShowPage))

(def ui-admin-core-node-link (comp/factory AdminCoreNodeLinkForm {:keyfn ::m.c.nodes/id}))

(form/defsc-form AdminCoreTxLinkForm
  [this {::m.c.transactions/keys [id tx-id]}]
  {fo/id           m.c.transactions/id
   fo/route-prefix "admin-core-tx-link"
   fo/attributes   [m.c.transactions/id m.c.transactions/tx-id]}
  (form-link this id tx-id :dinsro.ui.admin.core.transactions/ShowPage))

(def ui-admin-core-tx-link (comp/factory AdminCoreTxLinkForm {:keyfn ::m.c.transactions/id}))

(form/defsc-form AdminCurrencyLinkForm [this {::m.currencies/keys [id name]}]
  {fo/id           m.currencies/id
   fo/route-prefix "admin-currency-link"
   fo/attributes   [m.currencies/name]}
  (form-link this id name :dinsro.ui.admin.currencies/ShowPage))

(def ui-admin-currency-link (comp/factory AdminCurrencyLinkForm {:keyfn ::m.currencies/name}))

(form/defsc-form AdminDebitLinkForm [this {::m.debits/keys [id value]}]
  {fo/id           m.debits/id
   fo/route-prefix "admin-debit-link"
   fo/attributes   [m.debits/value]}
  (form-link this id value :dinsro.ui.admin.debits/ShowPage))

(def ui-admin-debit-link (comp/factory AdminDebitLinkForm {:keyfn ::m.debits/id}))

(form/defsc-form AdminEventLinkForm [this {::m.n.events/keys [id note-id]}]
  {fo/id           m.n.events/id
   fo/route-prefix "admin-event-link"
   fo/attributes   [m.n.events/note-id]}
  (form-link this id note-id :dinsro.ui.admin.nostr.events/ShowPage))

(def ui-admin-event-link (comp/factory AdminEventLinkForm {:keyfn ::m.n.events/note-id}))

(form/defsc-form AdminEventTagLinkForm [this {::m.n.event-tags/keys [id index]}]
  {fo/id           m.n.event-tags/id
   fo/route-prefix "admin-event-tags-link"
   fo/attributes   [m.n.event-tags/id]}
  (form-link this id (str index) :dinsro.ui.admin.nostr.event-tags/ShowPage))

(def ui-admin-event-tag-link (comp/factory AdminEventTagLinkForm {:keyfn ::m.n.event-tags/id}))

(form/defsc-form AdminFilterItemLinkForm [this {::m.n.filter-items/keys [id]}]
  {fo/id           m.n.filter-items/id
   fo/route-prefix "admin-filter-item-link"
   fo/attributes   [m.n.filter-items/id]}
  (form-link this id (str id) :dinsro.ui.admin.nostr.filter-items/ShowPage))

(def ui-admin-filter-item-link (comp/factory AdminFilterItemLinkForm {:keyfn ::m.n.filters/id}))

(form/defsc-form AdminFilterItemCountLinkForm [this {::m.n.filters/keys [id] ::j.n.filters/keys [item-count]}]
  {fo/id           m.n.filters/id
   fo/route-prefix "admin-filter-item-count-link"
   fo/attributes   [j.n.filters/item-count]}
  (form-link this id item-count :dinsro.ui.admin.nostr.filters.filter-items/SubPage))

(def ui-admin-filter-item-count-link (comp/factory AdminFilterItemCountLinkForm {:keyfn ::m.n.filters/id}))

(form/defsc-form AdminFilterLinkForm [this {::m.n.filters/keys [id index]}]
  {fo/id           m.n.filters/id
   fo/route-prefix "admin-filter-link"
   fo/attributes   [m.n.filters/index]}
  (form-link this id (str index) :dinsro.ui.admin.nostr.filters/ShowPage))

(def ui-admin-filter-link (comp/factory AdminFilterLinkForm {:keyfn ::m.n.filters/id}))

(form/defsc-form AdminInstanceLinkForm
  [this {::m.instances/keys [id] :as props}]
  {fo/id           m.instances/id
   fo/route-prefix "admin-instance-link"
   fo/attributes   [m.instances/id]}
  (log/trace :NodeLinkForm/starting {:id id :name name :props props})
  (form-link this id (str id) :dinsro.ui.admin.instances/IndexPage))

(def ui-admin-instance-link (comp/factory AdminInstanceLinkForm {:keyfn ::m.instances/id}))

(form/defsc-form AdminLnNodeLinkForm
  [this {::m.ln.nodes/keys [id name] :as props}]
  {fo/id           m.ln.nodes/id
   fo/route-prefix "admin-ln-node-link"
   fo/attributes   [m.ln.nodes/name]}
  (log/trace :NodeLinkForm/starting {:id id :name name :props props})
  (form-link this id name :dinsro.ui.admin.ln.nodes/ShowPage))

(def ui-admin-ln-node-link (comp/factory AdminLnNodeLinkForm {:keyfn ::m.ln.nodes/id}))

(form/defsc-form AdminLNPeerLinkForm [this {::m.ln.peers/keys [id remote-node]}]
  {fo/id           m.ln.peers/id
   fo/route-prefix "admin-ln-peer-link"
   fo/attributes   [m.ln.peers/remote-node]}
  (form-link this id remote-node :dinsro.ui.admin.ln.peers/ShowPage))

(def ui-admin-ln-peer-link (comp/factory AdminLNPeerLinkForm {:keyfn ::m.ln.peers/id}))

(form/defsc-form AdminNetworkLinkForm
  [this {::m.c.networks/keys [id name]}]
  {fo/id           m.c.networks/id
   fo/route-prefix "admin-network-link"
   fo/attributes   [m.c.networks/name]}
  (form-link this id name :dinsro.ui.admin.core.networks.addresses/SubPage))

(def ui-admin-network-link (comp/factory AdminNetworkLinkForm {:keyfn ::m.c.networks/id}))

(form/defsc-form AdminPubkeyLinkForm [this {::m.n.pubkeys/keys [id name hex]}]
  {fo/id           m.n.pubkeys/id
   fo/route-prefix "admin-pubkey-link"
   fo/attributes   [m.n.pubkeys/hex
                    m.n.pubkeys/name]}
  (form-link this id (or name hex) :dinsro.ui.admin.nostr.pubkeys.relays/SubPage))

(def ui-admin-pubkey-link (comp/factory AdminPubkeyLinkForm {:keyfn ::m.n.pubkeys/id}))

(form/defsc-form AdminPubkeyNameLinkForm [this {::j.n.pubkeys/keys [npub]
                                                ::m.n.pubkeys/keys [id name]}]
  {fo/id           m.n.pubkeys/id
   fo/route-prefix "admin-pubkey-name-link"
   fo/attributes   [m.n.pubkeys/hex
                    m.n.pubkeys/name
                    j.n.pubkeys/npub]}
  (form-link this id (or name npub "(unknown)") :dinsro.ui.admin.nostr.pubkeys.relays/SubPage))

(def ui-admin-pubkey-name-link (comp/factory AdminPubkeyNameLinkForm {:keyfn ::m.n.pubkeys/id}))

(form/defsc-form AdminRelayLinkForm [this {::m.n.relays/keys [id address]}]
  {fo/id           m.n.relays/id
   fo/route-prefix "relay-link"
   fo/attributes   [m.n.relays/address]}
  (form-link this id address :dinsro.ui.admin.nostr.relays.connections/SubPage))

(def ui-admin-relay-link (comp/factory AdminRelayLinkForm {:keyfn ::m.n.relays/id}))

(form/defsc-form AdminRateSourceLinkForm [this {::m.rate-sources/keys [id name]}]
  {fo/id         m.rate-sources/id
   fo/route-prefix "admin-rate-source-link"
   fo/attributes [m.rate-sources/name]}
  (form-link this id name :dinsro.ui.admin.rate-sources/ShowPage))

(def ui-admin-rate-source-link (comp/factory AdminRateSourceLinkForm {:keyfn ::m.rate-sources/id}))

(form/defsc-form AdminRelayConnectionCountLinkForm
  [this {::m.n.relays/keys [id]
         ::j.n.relays/keys [connection-count]}]
  {fo/id           m.n.relays/id
   fo/route-prefix "admin-relay-connection-count-link"
   fo/attributes   [j.n.relays/connection-count]}
  (form-link this id connection-count :dinsro.ui.admin.nostr.relays.connections/SubPage))

(def ui-admin-relay-connection-count-link
  (comp/factory AdminRelayConnectionCountLinkForm {:keyfn ::m.n.relays/id}))

(form/defsc-form AdminRelayRequestCountLinkForm
  [this {::m.n.relays/keys [id]
         ::j.n.relays/keys [request-count]}]
  {fo/id           m.n.relays/id
   fo/route-prefix "admin-relay-request-count-link"
   fo/attributes   [j.n.relays/request-count]}
  (form-link this id request-count :dinsro.ui.admin.nostr.relays.requests/SubPage))

(def ui-admin-relay-request-count-link
  (comp/factory AdminRelayRequestCountLinkForm {:keyfn ::m.n.relays/id}))

(form/defsc-form AdminRemoteNodeLinkForm [this {::m.ln.remote-nodes/keys [id pubkey]}]
  {fo/id           m.ln.remote-nodes/id
   fo/route-prefix "admin-remote-node-link"
   fo/attributes   [m.ln.remote-nodes/pubkey]}
  (form-link this id pubkey :dinsro.ui.admin.ln.remote-nodes/ShowPage))

(def ui-admin-remote-node-link (comp/factory AdminRemoteNodeLinkForm {:keyfn ::m.ln.remote-nodes/id}))

(form/defsc-form AdminRequestFilterCountLinkForm
  [this {::j.n.requests/keys [filter-count]
         ::m.n.requests/keys [id]}]
  {fo/id           m.n.requests/id
   fo/route-prefix "admin-request-filter-count-link"
   fo/attributes   [j.n.requests/filter-count]}
  (form-link this id filter-count :dinsro.ui.admin.nostr.requests.filters/SubPage))

(def ui-admin-request-filter-count-link (comp/factory AdminRequestFilterCountLinkForm {:keyfn ::m.n.requests/id}))

(form/defsc-form AdminRequestLinkForm [this {::m.n.requests/keys [id code]}]
  {fo/id           m.n.requests/id
   fo/route-prefix "admin-request-link"
   fo/attributes   [m.n.requests/code]}
  (form-link this id (str code) :dinsro.ui.admin.nostr.requests.filters/SubPage))

(def ui-admin-request-link (comp/factory AdminRequestLinkForm {:keyfn ::m.n.requests/id}))

(form/defsc-form AdminRequestRunCountLinkForm
  [this {::j.n.requests/keys [run-count]
         ::m.n.requests/keys [id]}]
  {fo/id           m.n.requests/id
   fo/route-prefix "admin-request-run-count-link"
   fo/attributes   [j.n.requests/run-count]}
  (form-link this id run-count :dinsro.ui.admin.nostr.requests.runs/SubPage))

(def ui-admin-request-run-count-link
  (comp/factory AdminRequestRunCountLinkForm {:keyfn ::m.n.requests/id}))

(form/defsc-form AdminRunLinkForm [this {::m.n.runs/keys [id status]}]
  {fo/id           m.n.runs/id
   fo/route-prefix "admin-run-link"
   fo/attributes   [m.n.runs/status]}
  (form-link this id (name status) :dinsro.ui.admin.nostr.runs/ShowPage))

(def ui-admin-run-link (comp/factory AdminRunLinkForm {:keyfn ::m.n.runs/id}))

(form/defsc-form AdminUserLinkForm [this {::m.users/keys [id name]}]
  {fo/id           m.users/id
   fo/route-prefix "admin-user-link"
   fo/attributes   [m.users/name]}
  (form-link this id name :dinsro.ui.admin.users/ShowPage))

(def ui-admin-user-link (comp/factory AdminUserLinkForm {:keyfn ::m.users/id}))

(form/defsc-form AdminUserAccountsCountLinkForm
  [this {::m.users/keys [id]
         ::j.users/keys [account-count]}]
  {fo/id           m.users/id
   fo/route-prefix "admin-user-accounts-count-link"
   fo/attributes   [m.users/id j.users/account-count]}
  (form-link this id account-count :dinsro.ui.admin.users.accounts/SubPage))

(def ui-admin-user-accounts-count-link
  (comp/factory AdminUserAccountsCountLinkForm {:keyfn ::m.users/id}))

(form/defsc-form AdminUserCategoriesCountLinkForm
  [this {::m.users/keys [id]
         ::j.users/keys [category-count]}]
  {fo/id           m.users/id
   fo/route-prefix "admin-user-categories-count-link"
   fo/attributes   [m.users/id j.users/category-count]}
  (form-link this id category-count :dinsro.ui.admin.users.categories/SubPage))

(def ui-admin-user-categories-count-link
  (comp/factory AdminUserCategoriesCountLinkForm {:keyfn ::m.users/id}))

(form/defsc-form AdminUserLnNodesCountLinkForm
  [this {::m.users/keys [id]
         ::j.users/keys [ln-node-count]}]
  {fo/id           m.users/id
   fo/route-prefix "admin-user-ln-nodes-count-link"
   fo/attributes   [m.users/id j.users/ln-node-count]}
  (form-link this id ln-node-count :dinsro.ui.admin.users.ln-nodes/SubPage))

(def ui-admin-user-ln-nodes-count-link
  (comp/factory AdminUserLnNodesCountLinkForm {:keyfn ::m.users/id}))

(form/defsc-form AdminUserTransactionsCountLinkForm
  [this {::m.users/keys [id]
         ::j.users/keys [transaction-count]}]
  {fo/id           m.users/id
   fo/route-prefix "admin-user-transactions-count-link"
   fo/attributes   [m.users/id j.users/transaction-count]}
  (form-link this id transaction-count :dinsro.ui.admin.users.transactions/SubPage))

(def ui-admin-user-transactions-count-link
  (comp/factory AdminUserTransactionsCountLinkForm {:keyfn ::m.users/id}))

(form/defsc-form AdminUserWalletsCountLinkForm
  [this {::m.users/keys [id]
         ::j.users/keys [wallet-count]}]
  {fo/id           m.users/id
   fo/route-prefix "admin-user-wallets-count-link"
   fo/attributes   [m.users/id j.users/wallet-count]}
  (form-link this id wallet-count :dinsro.ui.admin.users.wallets/SubPage))

(def ui-admin-user-wallets-count-link
  (comp/factory AdminUserWalletsCountLinkForm {:keyfn ::m.users/id}))

(form/defsc-form AdminWalletLinkForm [this {::m.c.wallets/keys [id name]}]
  {fo/id           m.c.wallets/id
   fo/route-prefix "admin-wallet-link"
   fo/attributes   [m.c.wallets/name]}
  (form-link this id name :dinsro.ui.admin.core.wallets/ShowPage))

(def ui-admin-wallet-link (comp/factory AdminWalletLinkForm {:keyfn ::m.c.wallets/id}))

(form/defsc-form AdminWitnessLinkForm [this {::m.n.witnesses/keys [id]}]
  {fo/id           m.n.witnesses/id
   fo/route-prefix "admin-witnesses-link"
   fo/attributes   [m.n.witnesses/id]}
  (form-link this id (str id) :dinsro.ui.admin.nostr.witnesses/ShowPage))

(def ui-admin-witness-link (comp/factory AdminWitnessLinkForm {:keyfn ::m.n.witnesses/id}))

(form/defsc-form BlockLinkForm
  [this {::m.c.blocks/keys [id hash]}]
  {fo/id           m.c.blocks/id
   fo/route-prefix "block-link"
   fo/attributes   [m.c.blocks/hash]}
  (log/info :BlockLinkForm/starting {:id id :hash hash})
  (form-link this id hash :dinsro.ui.core.blocks/ShowPage))

(def ui-block-link (comp/factory BlockLinkForm {:keyfn ::m.c.blocks/id}))

(form/defsc-form BlockHeightLinkForm
  [this {::m.c.blocks/keys [id height]}]
  {fo/id           m.c.blocks/id
   fo/route-prefix "block-height-link"
   fo/attributes   [m.c.blocks/height]}
  (form-link this id height :dinsro.ui.core.blocks/ShowPage))

(def ui-block-height-link (comp/factory BlockHeightLinkForm {:keyfn ::m.c.blocks/id}))

(form/defsc-form CategoryLinkForm
  [this {::m.categories/keys [id name]}]
  {fo/id         m.categories/id
   fo/route-prefix "category-link"
   fo/attributes [m.categories/name]}
  (form-link this id name :dinsro.ui.settings.categories/ShowPage))

(def ui-category-link (comp/factory CategoryLinkForm {:keyfn ::m.categories/id}))

(form/defsc-form ChainLinkForm [this {::m.c.chains/keys [id name]}]
  {fo/id         m.c.chains/id
   fo/route-prefix "chain-link"
   fo/attributes [m.c.chains/name]}
  (form-link this id name :dinsro.ui.core.chains/ShowPage))

(def ui-chain-link (comp/factory ChainLinkForm {:keyfn ::m.c.chains/id}))

(form/defsc-form ChannelLinkForm [this {::m.ln.channels/keys [id channel-point]}]
  {fo/id         m.ln.channels/id
   fo/route-prefix "channel-link"
   fo/attributes [m.ln.channels/channel-point]}
  (form-link this id channel-point :dinsro.ui.ln.channels/NewForm))

(def ui-channel-link (comp/factory ChannelLinkForm {:keyfn ::m.ln.channels/id}))

(form/defsc-form ContactsLinkForm
  [this {::m.contacts/keys [id name]}]
  {fo/id           m.contacts/id
   fo/route-prefix "contacts-link"
   fo/attributes   [m.contacts/id m.contacts/name]}
  (form-link this id name :dinsro.ui.contacts/ShowPage))

(def ui-contacts-link (comp/factory ContactsLinkForm {:keyfn ::m.contacts/id}))

(form/defsc-form CoreNodeLinkForm
  [this {::m.c.nodes/keys [id name] :as props}]
  {fo/id         m.c.nodes/id
   fo/route-prefix "core-node-link"
   fo/attributes [m.c.nodes/id m.c.nodes/name]}
  (log/trace :CoreNodeLinkForm/starting {:id id :name name :props props})
  (form-link this id (or name (str id)) :dinsro.ui.core.nodes/ShowPage))

(def ui-core-node-link (comp/factory CoreNodeLinkForm {:keyfn ::m.c.nodes/id}))

(form/defsc-form CorePeerLinkForm
  [this {::m.c.peers/keys [id addr]}]
  {fo/id         m.c.peers/id
   fo/route-prefix "core-peer-link"
   fo/attributes [m.c.peers/id m.c.peers/addr]}
  (form-link this id addr :dinsro.ui.core.peers/ShowPage))

(def ui-core-peer-link (comp/factory CorePeerLinkForm {:keyfn ::m.c.peers/id}))

(form/defsc-form CoreTxLinkForm
  [this {::m.c.transactions/keys [id tx-id]}]
  {fo/id         m.c.transactions/id
   fo/route-prefix "tx-link"
   fo/attributes [m.c.transactions/id m.c.transactions/tx-id]}
  (form-link this id tx-id :dinsro.ui.core.transactions/ShowPage))

(def ui-core-tx-link (comp/factory CoreTxLinkForm {:keyfn ::m.c.transactions/id}))

(form/defsc-form CurrencyLinkForm [this {::m.currencies/keys [id name]}]
  {fo/id         m.currencies/id
   fo/route-prefix "currency-link"
   fo/attributes [m.currencies/name]}
  (form-link this id name :dinsro.ui.currencies/ShowPage))

(def ui-currency-link (comp/factory CurrencyLinkForm {:keyfn ::m.currencies/name}))

(form/defsc-form DebitLinkForm [this {::m.debits/keys [id value]}]
  {fo/id         m.debits/id
   fo/route-prefix "debit-link"
   fo/attributes [m.debits/value]}
  (form-link this id value :dinsro.ui.debits/ShowPage))

(def ui-debit-link (comp/factory DebitLinkForm {:keyfn ::m.debits/name}))

(form/defsc-form EventCreatedLinkForm [this {::m.n.events/keys [id created-at]}]
  {fo/id           m.n.events/id
   fo/route-prefix "event-created-link"
   fo/attributes   [m.n.events/created-at]}
  (form-link this id created-at :dinsro.ui.nostr.events/ShowPage))

(def ui-event-created-link (comp/factory EventCreatedLinkForm {:keyfn ::m.n.events/note-id}))

(form/defsc-form EventLinkForm [this {::m.n.events/keys [id note-id]}]
  {fo/id           m.n.events/id
   fo/route-prefix "event-link"
   fo/attributes   [m.n.events/note-id]}
  (form-link this id note-id :dinsro.ui.nostr.events/ShowPage))

(def ui-event-link (comp/factory EventLinkForm {:keyfn ::m.n.events/note-id}))

(form/defsc-form EventTagLinkForm [this {::m.n.event-tags/keys [id index]}]
  {fo/id           m.n.event-tags/id
   fo/route-prefix "event-tags-link"
   fo/attributes   [m.n.event-tags/id]}
  (form-link this id (str index) :dinsro.ui.nostr.event-tags/ShowPage))

(def ui-event-tag-link (comp/factory EventTagLinkForm {:keyfn ::m.n.event-tags/id}))

(form/defsc-form FilterItemCountLinkForm [this {::m.n.filters/keys [id] ::j.n.filters/keys [item-count]}]
  {fo/id           m.n.filters/id
   fo/route-prefix "filter-link"
   fo/attributes   [j.n.filters/item-count]}
  (form-link this id item-count :dinsro.ui.nostr.filters.filter-items/SubPage))

(def ui-filter-item-count-link (comp/factory FilterItemCountLinkForm {:keyfn ::m.n.filters/id}))

(form/defsc-form FilterLinkForm [this {::m.n.filters/keys [id index]}]
  {fo/id           m.n.filters/id
   fo/route-prefix "filter-link"
   fo/attributes   [m.n.filters/index]}
  (form-link this id (str index) :dinsro.ui.nostr.filters/ShowPage))

(def ui-filter-link (comp/factory FilterLinkForm {:keyfn ::m.n.filters/id}))

(form/defsc-form InvoiceLinkForm [this {::m.ln.invoices/keys [id r-preimage]}]
  {fo/id           m.ln.invoices/id
   fo/route-prefix "invoice-link"
   fo/attributes   [m.ln.invoices/r-preimage]}
  (form-link this id r-preimage :dinsro.ui.ln.invoices/ShowPage))

(def ui-invoice-link (comp/factory InvoiceLinkForm {:keyfn ::m.ln.invoices/id}))

(form/defsc-form LNPeerLinkForm [this {::m.ln.peers/keys [id remote-node]}]
  {fo/id           m.ln.peers/id
   fo/route-prefix "ln-peer-link"
   fo/attributes   [m.ln.peers/remote-node]}
  (form-link this id remote-node :dinsro.ui.ln.peers/ShowPage))

(def ui-ln-peer-link (comp/factory LNPeerLinkForm {:keyfn ::m.ln.peers/id}))

(form/defsc-form NetworkLinkForm
  [this {::m.c.networks/keys [id name] :as props}]
  {fo/id           m.c.networks/id
   fo/route-prefix "network-link"
   fo/attributes   [m.c.networks/name]}
  (log/trace :NetworkLinkForm/starting {:id id :name name :props props})
  (form-link this id name :dinsro.ui.core.networks/ShowPage))

(def ui-network-link (comp/factory NetworkLinkForm {:keyfn ::m.c.networks/id}))

(form/defsc-form NavbarLinkForm
  [this {::m.navbars/keys [id]}]
  {fo/id           m.navbars/id
   fo/route-prefix "navbar-link"
   fo/attributes   [m.navbars/id]}
  (form-link this id (str id) :dinsro.ui.navbars/ShowPage))

(def ui-navbar-link (comp/factory NavbarLinkForm {:keyfn ::m.navbars/id}))

(form/defsc-form NavlinkLinkForm
  [this {::m.navlinks/keys [id label]}]
  {fo/id           m.navlinks/id
   fo/route-prefix "navlink-link"
   fo/attributes   [m.navlinks/id
                    m.navlinks/label]}
  (form-link this id label :dinsro.ui.navlinks/ShowPage))

(def ui-navlink-link (comp/factory NavlinkLinkForm {:keyfn ::m.navlinks/id}))

(form/defsc-form NodeLinkForm
  [this {::m.ln.nodes/keys [id name] :as props}]
  {fo/id           m.ln.nodes/id
   fo/route-prefix "ln-node-link"
   fo/attributes   [m.ln.nodes/name]}
  (log/trace :NodeLinkForm/starting {:id id :name name :props props})
  (form-link this id name :dinsro.ui.ln.nodes/ShowPage))

(def ui-node-link (comp/factory NodeLinkForm {:keyfn ::m.ln.nodes/id}))

(form/defsc-form RemoteNodeLinkForm [this {::m.ln.remote-nodes/keys [id pubkey]}]
  {fo/id           m.ln.remote-nodes/id
   fo/route-prefix "remote-node-link"
   fo/attributes   [m.ln.remote-nodes/pubkey]}
  (log/trace :RemoteNodeLinkForm/starting {:id id :pubkey pubkey})
  (form-link this id pubkey :dinsro.ui.ln.remote-nodes/ShowPage))

(def ui-remote-node-link (comp/factory RemoteNodeLinkForm {:keyfn ::m.ln.remote-nodes/id}))

(form/defsc-form RequestLinkForm [this {::m.n.requests/keys [id code]}]
  {fo/id           m.n.requests/id
   fo/route-prefix "request-link"
   fo/attributes   [m.n.requests/code]}
  (form-link this id (str code) :dinsro.ui.nostr.requests/ShowPage))

(def ui-request-link (comp/factory RequestLinkForm {:keyfn ::m.n.requests/id}))

(form/defsc-form PaymentsLinkForm [this {::m.ln.payments/keys [id payment-hash]}]
  {fo/id           m.ln.payments/id
   fo/route-prefix "payment-link"
   fo/attributes   [m.ln.payments/payment-hash]}
  (form-link this id payment-hash :dinsro.ui.ln.payments/ShowPage))

(def ui-payment-link (comp/factory PaymentsLinkForm {:keyfn ::m.ln.payments/id}))

(form/defsc-form PayReqLinkForm [this {::m.ln.payreqs/keys [id description]}]
  {fo/id           m.ln.payreqs/id
   fo/route-prefix "payreq-link"
   fo/attributes   [m.ln.payreqs/description]}
  (form-link this id description :dinsro.ui.ln.payreqs/ShowPage))

(def ui-payreq-link (comp/factory PayReqLinkForm {:keyfn ::m.ln.payreqs/id}))

(form/defsc-form PubkeyLinkForm [this {::m.n.pubkeys/keys [id name hex]}]
  {fo/id           m.n.pubkeys/id
   fo/route-prefix "pubkey-link"
   fo/attributes   [m.n.pubkeys/hex
                    m.n.pubkeys/name]}
  (form-link this id (or name hex) :dinsro.ui.nostr.pubkeys.events/SubPage))

(def ui-pubkey-link (comp/factory PubkeyLinkForm {:keyfn ::m.n.pubkeys/id}))

(form/defsc-form PubkeyNameLinkForm [this {::j.n.pubkeys/keys [npub]
                                           ::m.n.pubkeys/keys [id name]}]
  {fo/id           m.n.pubkeys/id
   fo/route-prefix "pubkey-name-link"
   fo/attributes   [m.n.pubkeys/hex
                    m.n.pubkeys/name
                    j.n.pubkeys/npub]}
  (form-link this id (or name npub "(unknown)") :dinsro.ui.nostr.pubkeys.events/SubPage))

(def ui-pubkey-name-link (comp/factory PubkeyNameLinkForm {:keyfn ::m.n.pubkeys/id}))

(form/defsc-form RateLinkForm [this {::m.rates/keys [id date]}]
  {fo/id           m.rates/id
   fo/route-prefix "rate-link"
   fo/attributes   [m.rates/date]}
  (form-link this id (str date) :dinsro.ui.admin.rates/ShowPage))

(def ui-rate-link (comp/factory RateLinkForm {:keyfn ::m.rates/id}))

(form/defsc-form RateSourceLinkForm [this {::m.rate-sources/keys [id name]}]
  {fo/id           m.rate-sources/id
   fo/route-prefix "rate-source-link"
   fo/attributes   [m.rate-sources/name]}
  (form-link this id name :dinsro.ui.settings.rate-sources/ShowPage))

(def ui-rate-source-link (comp/factory RateSourceLinkForm {:keyfn ::m.rate-sources/id}))

(form/defsc-form RateValueLinkForm [this {::m.rates/keys [id rate]}]
  {fo/id           m.rates/id
   fo/route-prefix "rate-value-link"
   fo/attributes   [m.rates/rate]}
  (form-link this id (str rate) :dinsro.ui.admin.rates/ShowPage))

(def ui-rate-value-link (comp/factory RateValueLinkForm {:keyfn ::m.rates/id}))

(form/defsc-form RelayLinkForm [this {::m.n.relays/keys [id address]}]
  {fo/id           m.n.relays/id
   fo/route-prefix "relay-link"
   fo/attributes   [m.n.relays/address]}
  (form-link this id address :dinsro.ui.nostr.relays/ShowPage))

(def ui-relay-link (comp/factory RelayLinkForm {:keyfn ::m.n.relays/id}))

(form/defsc-form RelayRequestCountLinkForm
  [this {::m.n.relays/keys [id]
         ::j.n.relays/keys [request-count]}]
  {fo/id           m.n.relays/id
   fo/route-prefix "relay-request-count-link"
   fo/attributes   [j.n.relays/request-count]}
  (form-link this id request-count :dinsro.ui.nostr.relays.requests/SubPage))

(def ui-relay-request-count-link
  (comp/factory RelayRequestCountLinkForm {:keyfn ::m.n.relays/id}))

(form/defsc-form TransactionLinkForm [this {::m.transactions/keys [id description]}]
  {fo/id           m.transactions/id
   fo/route-prefix "transaction-link"
   fo/attributes   [m.transactions/id m.transactions/description]}
  (form-link this id description :dinsro.ui.transactions/ShowPage))

(def ui-transaction-link (comp/factory TransactionLinkForm {:keyfn ::m.transactions/id}))

(form/defsc-form AdminTransactionLinkForm [this {::m.transactions/keys [id description]}]
  {fo/id           m.transactions/id
   fo/route-prefix "admin-transaction-link"
   fo/attributes   [m.transactions/id m.transactions/description]}
  (form-link this id description :dinsro.ui.admin.transactions/ShowPage))

(def ui-admin-transaction-link (comp/factory AdminTransactionLinkForm {:keyfn ::m.transactions/id}))

(form/defsc-form UserLinkForm [this {::m.users/keys [id name]}]
  {fo/id           m.users/id
   fo/route-prefix "user-link"
   fo/attributes   [m.users/name]}
  (form-link this id name :dinsro.ui.admin.users.accounts/SubPage))

(def ui-user-link (comp/factory UserLinkForm {:keyfn ::m.users/id}))

(form/defsc-form WalletAddressLinkForm [this {::m.c.wallet-addresses/keys [id address]}]
  {fo/id         m.c.wallet-addresses/id
   fo/route-prefix "wallet-address-link"
   fo/attributes [m.c.wallet-addresses/address]}
  (form-link this id address :dinsro.ui.core.wallet-addresses/ShowPage))

(def ui-wallet-address-link (comp/factory WalletAddressLinkForm {:keyfn ::m.c.wallet-addresses/id}))

(form/defsc-form WalletLinkForm [this {::m.c.wallets/keys [id name]}]
  {fo/id         m.c.wallets/id
   fo/route-prefix "wallet-link"
   fo/attributes [m.c.wallets/name]}
  (form-link this id name :dinsro.ui.core.wallets/ShowPage))

(def ui-wallet-link (comp/factory WalletLinkForm {:keyfn ::m.c.wallets/id}))

(form/defsc-form WitnessLinkForm [this {::m.n.witnesses/keys [id]}]
  {fo/id           m.n.witnesses/id
   fo/route-prefix "witnesses-link"
   fo/attributes   [m.n.witnesses/id]}
  (form-link this id (str id) :dinsro.ui.nostr.witnesses/ShowPage))

(def ui-witness-link (comp/factory WitnessLinkForm {:keyfn ::m.n.witnesses/id}))

(form/defsc-form WordLinkForm [this {::m.c.words/keys [id word]}]
  {fo/id         m.c.words/id
   fo/route-prefix "word-link"
   fo/attributes [m.c.words/word]}
  (form-link this id word :dinsro.ui.core.words/ShowPage))

(def ui-word-link (comp/factory WordLinkForm {:keyfn ::m.c.words/id}))

(defn report-link
  [key-fn link-fn]
  (fn [this value]
    (let [{:ui/keys [current-rows]} (comp/props this)]
      (if-let [row (first (filter #(= (key-fn %) value) current-rows))]
        (link-fn row)
        (dom/p {} "not found")))))
