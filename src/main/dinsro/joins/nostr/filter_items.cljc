(ns dinsro.joins.nostr.filter-items
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.joins :as j]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   #?(:clj [dinsro.queries.nostr.filter-items :as q.n.filter-items])
   #?(:clj [dinsro.queries.nostr.relays :as q.n.relays])
   #?(:clj [dinsro.queries.nostr.requests :as q.n.requests])
   [lambdaisland.glogc :as log]))

;; [../../actions/nostr/filter_items.clj]
;; [../../model/nostr/filter_items.cljc]
;; [../../queries/nostr/filter_items.clj]

(def join-info
  (merge
   {:idents m.n.filter-items/idents}
   #?(:clj {:indexer q.n.filter-items/index-ids
            :counter q.n.filter-items/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.filter-items/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.filter-items/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.filter-items/id
   ao/pc-output [{::index [:total {:results [::m.n.filter-items/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr relay ::relay :ref
  {ao/target           ::m.n.relays/id
   ao/pc-input         #{::m.n.filter-items/id}
   ao/pc-output        [{::relay [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [_ {::m.n.filter-items/keys [id]}]
     (log/info :relay/starting {:id id})
     (let [relay-id #?(:clj (q.n.relays/find-by-filter-item id)
                       :cljs (do (comment id) nil))]
       (log/info :relay/starting {:relay-id relay-id})
       {::relay (when relay-id (m.n.relays/ident relay-id))}))
   ::report/column-EQL {::relay [::m.n.relays/id ::m.n.relays/address]}})

(defattr request ::request :ref
  {ao/target    ::m.n.requests/id
   ao/pc-input  #{::m.n.filter-items/id}
   ao/pc-output [{::request [::m.n.requests/id]}]
   ao/pc-resolve
   (fn [_ {::m.n.filter-items/keys [id]}]
     (let [request-id #?(:clj (q.n.requests/find-by-filter-item id)
                         :cljs (do (comment id) nil))]
       {::request (when request-id (m.n.requests/ident request-id))}))
   ::report/column-EQL {::request [::m.n.requests/id ::m.n.requests/code]}})

(def attributes [admin-index index request relay])
