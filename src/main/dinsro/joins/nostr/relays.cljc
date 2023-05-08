(ns dinsro.joins.nostr.relays
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   #?(:clj [dinsro.queries.nostr.connections :as q.n.connections])
   #?(:clj [dinsro.queries.nostr.relays :as q.n.relays])
   #?(:clj [dinsro.queries.nostr.requests :as q.n.requests])
   #?(:clj [dinsro.queries.nostr.subscriptions :as q.n.subscriptions])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/relays.clj][Actions]]
;; [[../../model/nostr/relays.cljc][Model]]
;; [[../../queries/nostr/connections.clj]]
;; [[../../queries/nostr/relays.clj][Queries]]
;; [[../../ui/nostr/relays.cljs][UI]]

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.relays/id
   ao/pc-output [{::admin-index [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.relays/index-ids) :cljs [])]
       {::admin-index (m.n.relays/idents ids)}))})

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

(defattr index ::index :ref
  {ao/target    ::m.n.relays/id
   ao/pc-output [{::index [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (let [ids #?(:clj (q.n.relays/index-ids query-params)
                  :cljs (do (comment query-params) []))]
       (log/info :index/starting {:ids ids})
       (let [total #?(:clj (q.n.relays/count-ids query-params) :cljs 0)]
         {::index {:total total :results (m.n.relays/idents ids)}})))})

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

(defattr subscriptions ::subscriptions :ref
  {ao/cardinality :many
   ao/identities  #{::m.n.relays/id}
   ao/pc-input    #{::m.n.relays/id}
   ao/target      ::m.n.subscriptions/id
   ao/pc-output   {::subscriptions [::m.n.subscriptions/id]}
   ao/pc-resolve
   (fn [_env params]
     (log/info :subscription-count/starting {:params params})
     (let [relay-id (::m.n.relays/id params)
           ids      #?(:clj  (q.n.subscriptions/find-by-relay relay-id)
                       :cljs (do (comment relay-id) []))]
       {::subscriptions (m.n.subscriptions/idents ids)}))})

(defattr subscription-count ::subscription-count :int
  {ao/identities #{::m.n.relays/id}
   ao/pc-input   #{::subscriptions}
   ao/pc-resolve (fn [_ {::keys [subscriptions]}] {::subscription-count (count subscriptions)})})

(def attributes
  [admin-index connection-count connections
   index request-count requests subscription-count subscriptions])
