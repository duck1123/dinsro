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

(defonce menus-atom (atom {}))

(defn defmenu
  [key options]
  (swap! menus-atom assoc key options))

;; The top sub menu on admin pages
(defmenu :admin
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
               :navlinks]})

(defmenu :admin-core
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
               :admin-core-wallets
               :admin-core-wallet-addresses]})

(defmenu
  :admin-core-networks
  {::parent   :admin-core
   ::router   :dinsro.ui.admin.core.networks/Router
   ::children [:admin-core-networks-show-addresses
               :admin-core-networks-show-blocks
               :admin-core-networks-show-ln-nodes
               :admin-core-networks-show-core-nodes
               :admin-core-networks-show-wallets]})

(defmenu
  :admin-core-nodes
  {::parent   :admin-core
   ::router   :dinsro.ui.admin.core.nodes/Router
   ::children [:admin-core-nodes-show-peers
               :admin-core-nodes-show-blocks]})

(defmenu
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
               :admin-ln-remote-nodes]})

(defmenu
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
               :admin-nostr-witnesses]})

(defmenu
  :admin-nostr-connections
  {::parent   :admin-nostr
   ::router   :dinsro.ui.admin.nostr.connections/Router
   ::children [:admin-nostr-connections-show-runs]})

(defmenu
  :admin-nostr-pubkeys
  {::parent   :admin-nostr
   ::router   :dinsro.ui.admin.nostr.pubkeys/Router
   ::children [:admin-nostr-pubkeys-show-relays]})

(defmenu
  :admin-nostr-relays
  {::parent   :admin-nostr
   ::router   :dinsro.ui.admin.nostr.relays/Router
   ::children [:admin-nostr-relays-show-connections
               :admin-nostr-relays-show-requests
               :admin-nostr-relays-show-events
               :admin-nostr-relays-show-pubkeys
               :admin-nostr-relays-show-runs
               :admin-nostr-relays-show-witnesses]})

(defmenu
  :admin-nostr-requests
  {::parent   :admin-nostr
   ::router   :dinsro.ui.admin.nostr.requests/Router
   ::children [:admin-nostr-requests-show-filters
               :admin-nostr-requests-show-items
               :admin-nostr-requests-show-runs
               :admin-nostr-requests-show-connections]})

(defmenu
  :admin-users
  {::parent   :admin
   ::router   :dinsro.ui.admin.users/Router
   ::children [:admin-users-show-accounts
               :admin-users-show-categories
               :admin-users-show-debits
               :admin-users-show-ln-nodes
               :admin-users-show-pubkeys
               :admin-users-show-transactions
               :admin-users-show-user-pubkeys
               :admin-users-show-wallets]})

(defmenu
  :core-chains
  {::parent   :core
   ::router   :dinsro.ui.core.chains/Router
   ::children [:core-chains-show-networks]})

(defmenu
  :core-networks
  {::parent   :core
   ::router   :dinsro.ui.core.networks/Router
   ::children [:core-networks-show-addresses
               :core-networks-show-blocks
               :core-networks-show-ln-nodes
               :core-networks-show-core-nodes
               :core-networks-show-wallets]})

(defmenu
  :core-nodes
  {::parent   :core
   ::router   :dinsro.ui.core.nodes/Router
   ::children [:core-nodes-show-peers
               :core-nodes-show-blocks]})

(defmenu
  :currencies
  {::parent   :root
   ::router   :dinsro.ui.currencies/Router
   ::children [:currencies-show-rate-sources
               :currencies-show-accounts
               :currencies-show-rates]})

(defmenu
  :ln-nodes
  {::parent   :ln
   ::router   :dinsro.ui.ln.nodes/Router
   ::children [:ln-nodes-show-accounts
               :ln-nodes-show-addresses
               :ln-nodes-show-channels
               :ln-nodes-show-peers
               :ln-nodes-show-remote-nodes
               :ln-nodes-show-wallet-addresses]})

(defmenu
;; Main top bar
  :main
  {::parent   :root
   ::children [:accounts
               :transactions
               :contacts
               :nostr-events
               :settings
               :admin]})

(defmenu
  :nostr-connections
  {::parent   :nostr
   ::children [:nostr-connections-show-runs]})

(defmenu
  :nostr-event-tags
  {::parent   :nostr
   ::children [:nostr-event-tags-show-relays]})

(defmenu
  :nostr-events
  {::parent   :nostr
   ::children [:nostr-events-show-tags
               :nostr-events-show-witnesses
               :nostr-events-show-relays]})

(defmenu
  :nostr-filters
  {::parent   :nostr
   ::children [:nostr-filters-show-items]})

(defmenu
  :nostr-relays
  {::parent   :nostr
   ::children [:nostr-relays-show-connections
               :nostr-relays-show-requests
               :nostr-relays-show-events
               :nostr-relays-show-pubkeys
               :nostr-relays-show-runs
               :nostr-relays-show-witnesses]})

(defmenu
  :nostr-requests
  {::parent   :nostr
   ::children [:nostr-requests-show-filters
               :nostr-requests-show-items
               :nostr-requests-show-runs
               :nostr-requests-show-connections]})

(defmenu
  :nostr-pubkeys
  {::parent   :nostr
   ::children [:nostr-pubkeys-show-events
               :nostr-pubkeys-show-relays
               :nostr-pubkeys-show-items]})

(defmenu
  :nostr-runs
  {::parent   :nostr
   ::children [:nostr-runs-show-witnesses]})

(defmenu
  :nostr-subscriptions
  {::parent   :nostr
   ::children [:nostr-subscriptions-show-pubkeys]})

(defmenu
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
               :transactions]})

(defmenu
  :settings-ln
  {::parent   :settings
   ::children [:settings-ln-dashboard
               :settings-ln-nodes
                ;; :settings-ln-channels
               :settings-ln-payments
               :settings-ln-payreqs
               :settings-ln-remote-nodes]})

(defmenu
  :settings-ln-nodes
  {::parent   :settings-ln
   ::children [:settings-ln-nodes-show-accounts
               :settings-ln-nodes-show-addresses
               :settings-ln-nodes-show-channels
               :settings-ln-nodes-show-peers
               :settings-ln-nodes-show-remote-nodes
               :settings-ln-nodes-show-wallet-addresses]})

(defmenu
  :settings
  {::parent   :root
   ::children [:settings-dashboard
               :settings-core
               :settings-ln
               :settings-rate-sources
               :settings-categories]})

(defmenu
  :settings-rate-sources
  {::parent   :settings
   ::children [:settings-rate-sources-show-accounts]})

(defmenu
  :sidebar
  {::parent   :root
   ::children [:home
               :accounts
               :transactions
               :contacts
               :nostr-events
               :settings
               :admin]})

(defmenu
  :unauth
  {::parent   :root
   ::children [:login
               :registration]})

(def menus
  {})

(defattr id ::id :uuid
  {ao/identity? true})

(defattr children ::children :ref
  {ao/identities #{::id}
   ao/target     ::m.navlinks/id
   ao/pc-input   #{::id}
   ao/pc-output  [{::children [::m.navlinks/id]}]
   ao/pc-resolve (fn [_env {::keys [id]}]
                   (if-let [navbar (get @menus-atom id)]
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
       (if-let [navbar (get @menus-atom id)]
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
