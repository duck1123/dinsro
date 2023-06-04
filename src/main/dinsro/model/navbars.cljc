(ns dinsro.model.navbars
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.navlinks :as m.navlinks]))

#?(:cljs (comment ::pc/_ ::m.navlinks/id))

(def menus
  {:admin                   [:admin-nostr
                             :admin-users
                             :admin-core
                             :admin-ln
                             :admin-categories
                             :admin-accounts
                             :admin-currencies
                             :admin-transactions
                             :admin-debits
                             :admin-rate-sources
                             :admin-rates]
   :admin-core              [:admin-core-dashboard
                             :admin-core-addresses
                             :admin-core-blocks
                             :admin-core-chains
                             :admin-core-mnemonics
                             :admin-core-networks
                             :admin-core-nodes
                             :admin-core-peers
                             :admin-core-transactions
                             :admin-core-wallets]
   :admin-ln                [:admin-ln-dashboard
                             :admin-ln-accounts
                             :admin-ln-channels
                             :admin-ln-invoices
                             :admin-ln-nodes
                             :admin-ln-payreqs
                             :admin-ln-peers
                             :admin-ln-remote-nodes]
   :admin-nostr             [:admin-nostr-dashboard
                             :admin-nostr-relays
                             :admin-nostr-pubkeys
                             :admin-nostr-events
                             :admin-nostr-filters
                             :admin-nostr-badge-acceptances
                             :admin-nostr-badge-awards
                             :admin-nostr-badge-definitions
                             :admin-nostr-requests
                             :admin-nostr-connections
                             :admin-nostr-filter-items
                             :admin-nostr-runs
                             :admin-nostr-witnesses]
   :admin-nostr-connections [:admin-nostr-connections-runs]
   :admin-nostr-relays      [:admin-nostr-relays-connections
                             :admin-nostr-relays-requests
                             :admin-nostr-relays-events
                             :admin-nostr-relays-pubkeys
                             :admin-nostr-relays-runs
                             :admin-nostr-relays-witnesses]
   :admin-nostr-requests    [:admin-nostr-requests-filters
                             :admin-nostr-requests-items
                             :admin-nostr-requests-runs
                             :admin-nostr-requests-connections]
   :admin-users             [:admin-users-accounts
                             :admin-users-categories
                             :admin-users-debits
                             :admin-users-ln-nodes
                             :admin-users-pubkeys
                             :admin-users-transactions
                             :admin-users-user-pubkeys
                             :admin-users-wallets]
   :core-chains             [:core-chains-networks]
   :core-networks           [:core-networks-addresses
                             :core-networks-blocks
                             :core-networks-ln-nodes
                             :core-networks-core-nodes
                             :core-networks-wallets]
   :core-nodes              [:core-nodes-peers
                             :core-nodes-blocks]
   :currencies              [:currencies-rate-sources
                             :currencies-accounts
                             :currencies-rates]
   :ln-nodes                [:ln-nodes-accounts
                             :ln-nodes-addresses
                             :ln-nodes-channels
                             :ln-nodes-peers
                             :ln-nodes-remote-nodes
                             :ln-nodes-wallet-addresses]
   :main                    [:accounts
                             :transactions
                             :contacts
                             :nostr-events
                             :settings
                             :admin]
   :nostr-connections       [:nostr-connections-runs]
   :nostr-event-tags        [:nostr-event-tags-relays]
   :nostr-events            [:nostr-events-tags
                             :nostr-events-witnesses
                             :nostr-events-relays]
   :nostr-filters           [:nostr-filters-items]
   :nostr-relays            [:nostr-relays-connections
                             :nostr-relays-requests
                             :nostr-relays-events
                             :nostr-relays-pubkeys
                             :nostr-relays-runs
                             :nostr-relays-witnesses]
   :nostr-requests          [:nostr-requests-filters
                             :nostr-requests-items
                             :nostr-requests-runs
                             :nostr-requests-connections]
   :nostr-pubkeys           [:nostr-pubkeys-events
                             :nostr-pubkeys-relays
                             :nostr-pubkeys-items]
   :nostr-runs              [:nostr-runs-witnesses]
   :nostr-subscriptions     [:nostr-subscriptions-pubkeys]
   :settings-ln             [:settings-ln-dashboard
                             :settings-ln-nodes
                             ;; :settings-ln-channels
                             :settings-ln-payments
                             :settings-ln-payreqs
                             :settings-ln-remote-nodes]
   :settings-ln-nodes       [:settings-ln-nodes-accounts
                             :settings-ln-nodes-addresses
                             :settings-ln-nodes-channels
                             :settings-ln-nodes-peers
                             :settings-ln-nodes-remote-nodes
                             :settings-ln-nodes-wallet-addresses]
   :settings                [:settings-dashboard
                             :settings-core
                             :settings-ln
                             :settings-rate-sources
                             :settings-categories]
   :settings-rate-sources   [:settings-rate-sources-accounts]
   :sidebar                [:home
                            :accounts
                            :transactions
                            :contacts
                            :nostr-events
                            :settings
                            :admin]
   :unauth                  [:login
                             :registration]})

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(s/def ::id keyword?)

#?(:clj
   (pc/defresolver items
     [_env props]
     {::pc/input  #{::id}
      ::pc/output [{::items [::m.navlinks/id]}]}
     (let [{::keys [id]} props
           ids           (get menus id [])]
       {::items (m.navlinks/idents ids)})))

#?(:clj
   (pc/defresolver authenticated
     [_env _props]
     {::pc/input #{::id}
      ::pc/output [{::authenticated [::id]}]}
     {::authenticated {::id :main}}))

#?(:clj
   (pc/defresolver sidebar
     [_env _props]
     {::pc/input #{}
      ::pc/output [{::sidebar [::id]}]}
     {::sidebar {::id :sidebar}}))

#?(:clj
   (pc/defresolver unauthenticated
     [_env _props]
     {::pc/input #{}
      ::pc/output [{::unauthenticated [::id]}]}
     {::unauthenticated {::id :unauth}}))

#?(:clj (def resolvers [items authenticated sidebar unauthenticated]))
