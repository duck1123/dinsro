(ns dinsro.model.navbars
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.navlinks :as m.navlinks]
   [lambdaisland.glogc :as log]))

;; [[../joins/navbars.cljc]]
;; [[../ui/navbars.cljs]]

#?(:cljs (comment ::m.navlinks/id))

(def menus
  {;; The top sub menu on admin pages
   :admin
   {::parent   :root
    ::router   :dinsro.ui.admin/Router
    ::children [:admin-users
                :admin-core
                :admin-ln
                :admin-nostr
                :admin-categories
                :admin-accounts
                :admin-currencies
                :admin-transactions
                :admin-debits
                :admin-rate-sources
                :admin-rates
                :admin-models
                :navbars
                :navlinks]}

   :admin-core
   {::parent   :admin
    ::router   :dinsro.ui.admin.core/Router
    ::children [:admin-core-dashboard
                :admin-core-addresses
                :admin-core-blocks
                :admin-core-chains
                :admin-core-mnemonics
                :admin-core-networks
                :admin-core-nodes
                :admin-core-peers
                :admin-core-transactions
                :admin-core-wallets]}

   :admin-ln
   {::parent   :admin
    ::router   :dinsro.ui.admin.ln/Router
    ::children [:admin-ln-dashboard
                :admin-ln-accounts
                :admin-ln-channels
                :admin-ln-invoices
                :admin-ln-nodes
                :admin-ln-payreqs
                :admin-ln-peers
                :admin-ln-remote-nodes]}

   :admin-nostr
   {::parent   :admin
    ::router   :dinsro.ui.admin.nostr/Router
    ::children [:admin-nostr-dashboard
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
                :admin-nostr-witnesses]}

   :admin-nostr-connections
   {::parent   :admin-nostr
    ::router   :dinsro.ui.admin.nostr.connections/Router
    ::children [:admin-nostr-connections-runs]}

   :admin-nostr-relays
   {::parent   :admin-nostr
    ::router   :dinsro.ui.admin.nostr.relays/Router
    ::children [:admin-nostr-relays-connections
                :admin-nostr-relays-requests
                :admin-nostr-relays-events
                :admin-nostr-relays-pubkeys
                :admin-nostr-relays-runs
                :admin-nostr-relays-witnesses]}

   :admin-nostr-requests
   {::parent   :admin-nostr
    ::router   :dinsro.ui.admin.nostr.requests/Router
    ::children [:admin-nostr-requests-filters
                :admin-nostr-requests-items
                :admin-nostr-requests-runs
                :admin-nostr-requests-connections]}

   :admin-users
   {::parent   :admin
    ::router   :dinsro.ui.admin.users/Router
    ::children [:admin-users-accounts
                :admin-users-categories
                :admin-users-debits
                :admin-users-ln-nodes
                :admin-users-pubkeys
                :admin-users-transactions
                :admin-users-user-pubkeys
                :admin-users-wallets]}

   :core-chains
   {::parent   :core
    ::router   :dinsro.ui.core.chains/Router
    ::children [:core-chains-networks]}

   :core-networks
   {::parent   :core
    ::router   :dinsro.ui.core.networks/Router
    ::children [:core-networks-addresses
                :core-networks-blocks
                :core-networks-ln-nodes
                :core-networks-core-nodes
                :core-networks-wallets]}

   :core-nodes
   {::parent   :core
    ::router :dinsro.ui.core.nodes/Router
    ::children [:core-nodes-peers
                :core-nodes-blocks]}

   :currencies
   {::parent   :root
    ::router :dinsro.ui.currencies/Router
    ::children [:currencies-rate-sources
                :currencies-accounts
                :currencies-rates]}

   :ln-nodes
   {::parent   :ln
    ::router :dinsro.ui.ln.nodes/Router
    ::children [:ln-nodes-accounts
                :ln-nodes-addresses
                :ln-nodes-channels
                :ln-nodes-peers
                :ln-nodes-remote-nodes
                :ln-nodes-wallet-addresses]}

   ;; Main top bar
   :main
   {::parent   :root
    ::children [:accounts
                :transactions
                :contacts
                :nostr-events
                :settings
                :admin]}

   :nostr-connections
   {::parent   :nostr
    ::children [:nostr-connections-runs]}

   :nostr-event-tags
   {::parent   :nostr
    ::children [:nostr-event-tags-relays]}

   :nostr-events
   {::parent   :nostr
    ::children [:nostr-events-tags
                :nostr-events-witnesses
                :nostr-events-relays]}

   :nostr-filters
   {::parent   :nostr
    ::children [:nostr-filters-items]}

   :nostr-relays
   {::parent   :nostr
    ::children [:nostr-relays-connections
                :nostr-relays-requests
                :nostr-relays-events
                :nostr-relays-pubkeys
                :nostr-relays-runs
                :nostr-relays-witnesses]}

   :nostr-requests
   {::parent   :nostr
    ::children [:nostr-requests-filters
                :nostr-requests-items
                :nostr-requests-runs
                :nostr-requests-connections]}

   :nostr-pubkeys
   {::parent   :nostr
    ::children [:nostr-pubkeys-events
                :nostr-pubkeys-relays
                :nostr-pubkeys-items]}

   :nostr-runs
   {::parent   :nostr
    ::children [:nostr-runs-witnesses]}

   :nostr-subscriptions
   {::parent   :nostr
    ::children [:nostr-subscriptions-pubkeys]}

   :root
   {::parent   nil
    ::router   :dinsro.ui/RootRouter
    ::children [:accounts
                :admin
                :contacts
                :currencies
                :home
                :login
                :navbars
                :navlinks
                :nostr
                :nodes
                :registration
                :settings
                :transactions]}

   :settings-ln
   {::parent   :settings
    ::children [:settings-ln-dashboard
                :settings-ln-nodes
                ;; :settings-ln-channels
                :settings-ln-payments
                :settings-ln-payreqs
                :settings-ln-remote-nodes]}

   :settings-ln-nodes
   {::parent   :settings-ln
    ::children [:settings-ln-nodes-accounts
                :settings-ln-nodes-addresses
                :settings-ln-nodes-channels
                :settings-ln-nodes-peers
                :settings-ln-nodes-remote-nodes
                :settings-ln-nodes-wallet-addresses]}

   :settings
   {::parent   :root
    ::children [:settings-dashboard
                :settings-core
                :settings-ln
                :settings-rate-sources
                :settings-categories]}

   :settings-rate-sources
   {::parent   :settings
    ::children [:settings-rate-sources-accounts]}

   :sidebar
   {::parent   :root
    ::children [:home
                :accounts
                :transactions
                :contacts
                :nostr-events
                :settings
                :admin]}

   :unauth
   {::parent   :root
    ::children [:login
                :registration]}})

(defattr id ::id :uuid
  {ao/identity? true})

(defattr children ::children :ref
  {ao/identities #{::id}
   ao/target     ::m.navlinks/id
   ao/pc-input   #{::id}
   ao/pc-output  [{::children [::m.navlinks/id]}]
   ao/pc-resolve (fn [_env {::keys [id]}]
                   (if-let [navbar (get menus id)]
                     {::children (m.navlinks/idents (::children navbar []))}
                     {}))})

(defattr child-count ::child-count :number
  {ao/identities #{::id}
   ao/pc-input   #{::children}
   ao/pc-output  [::child-count]
   ao/pc-resolve (fn [_ {::keys [children]}] {::child-count (count children)})})

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(s/def ::id keyword?)
(defattr parent ::parent :ref
  {ao/identities #{::id}
   ao/target     ::id
   ao/pc-input   #{::id}
   ao/pc-output  [{::parent [::id]}]
   ao/pc-resolve
   (fn [_env props]
     (log/info :parent/starting {:props props})
     (let [{::keys [id]} props]
       (if-let [navbar (get menus id)]
         (let [{::keys [parent]} navbar]
           {::parent (when parent (ident parent))})
         {})))})

(defattr authenticated ::authenticated :ref
  {ao/target     ::id
   ao/pc-output  [{::authenticated [::id]}]
   ao/pc-resolve (fn [_env _props] {::authenticated {::id :main}})})

(defattr sidebar ::sidebar :ref
  {ao/target     ::id
   ao/pc-output  [{::sidebar [::id]}]
   ao/pc-resolve (fn [_env _props] {::sidebar {::id :sidebar}})})

(defattr unauthenticated ::unauthenticated :ref
  {ao/target     ::id
   ao/pc-output  [{::unauthenticated [::id]}]
   ao/pc-resolve (fn [_env _props] {::unauthenticated {::id :unauth}})})

(def attributes [id children parent authenticated sidebar unauthenticated
                 child-count])
