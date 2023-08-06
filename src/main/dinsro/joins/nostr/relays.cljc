(ns dinsro.joins.nostr.relays
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   #?(:clj [dinsro.queries.nostr.connections :as q.n.connections])
   #?(:clj [dinsro.queries.nostr.relays :as q.n.relays])
   #?(:clj [dinsro.queries.nostr.requests :as q.n.requests])
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/relays.clj]]
;; [[../../model/nostr/relays.cljc]]
;; [[../../queries/nostr/connections.clj]]
;; [[../../queries/nostr/relays.clj]]
;; [[../../ui/nostr/relays.cljs]]

(def join-info
  (merge
   {:idents m.n.relays/idents}
   #?(:clj {:indexer q.n.relays/index-ids
            :counter q.n.relays/count-ids})))

(defattr active-connection-count ::active-connection-count :int
  {ao/identities #{::m.n.relays/id}
   ao/pc-input   #{::active-connections}
   ao/pc-resolve (fn [_ {::keys [connections]}] {::active-connection-count (count connections)})})

(defattr active-connections ::active-connections :ref
  {ao/cardinality :many
   ao/identities #{::m.n.relays/id}
   ao/pc-input #{::m.n.relays/id}
   ao/target ::m.n.connections/id
   ao/pc-resolve
   (fn [_env params]
     (let [relay-id (::m.n.relays/id params)]
       (log/info :request-count/starting {:relay-id relay-id})
       (let [ids #?(:clj  (q.n.connections/index-ids {::m.n.relays/id relay-id
                                                      :active         true})
                    :cljs (do (comment relay-id) []))]
         {::active-connections (m.n.requests/idents ids)})))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.relays/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.relays/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.relays/id
   ao/pc-output [{::index [:total {:results [::m.n.relays/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr connection-count ::connection-count :int
  {ao/identities #{::m.n.relays/id}
   ao/pc-input   #{::connections}
   ao/pc-resolve (fn [_ {::keys [connections]}] {::connection-count (count connections)})})

(defattr connections ::connections :ref
  {ao/cardinality :many
   ao/identities #{::m.n.relays/id}
   ao/pc-input #{::m.n.relays/id}
   ao/target ::m.n.connections/id
   ao/pc-resolve
   (fn [_env params]
     (let [relay-id (::m.n.relays/id params)]
       (log/info :request-count/starting {:relay-id relay-id})
       (let [ids #?(:clj  (q.n.connections/index-ids {::m.n.relays/id relay-id})
                    :cljs (do (comment relay-id) []))]
         {::connections (m.n.requests/idents ids)})))})

(defattr requests ::requests :ref
  {ao/cardinality :many
   ao/identities  #{::m.n.relays/id}
   ao/pc-input    #{::m.n.relays/id}
   ao/target      ::m.n.requests/id
   ao/pc-resolve
   (fn [_env params]
     (let [relay-id (::m.n.relays/id params)]
       (log/info :request-count/starting {:relay-id relay-id})
       (let [ids #?(:clj  (q.n.requests/find-by-relay relay-id)
                    :cljs (do (comment relay-id) []))]
         {::requests (m.n.requests/idents ids)})))})

(defattr request-count ::request-count :int
  {ao/identities #{::m.n.relays/id}
   ao/pc-input   #{::requests}
   ao/pc-resolve (fn [_ {::keys [requests]}] {::request-count (count requests)})})

(def attributes
  [active-connection-count
   active-connections
   admin-index connection-count connections
   index request-count requests])
