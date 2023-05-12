(ns dinsro.joins.nostr.witnesses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.joins :as j]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   #?(:clj [dinsro.queries.nostr.relays :as q.n.relays])
   #?(:clj [dinsro.queries.nostr.witnesses :as q.n.witnesses])
   [dinsro.specs]))

;; [../../model/nostr/witnesses.cljc]
;; [../../queries/nostr/witnesses.clj]
;; [../../ui/admin/nostr/witnesses.cljs]
;; [../../ui/nostr/events/witnesses.cljs]

(def join-info
  (merge
   {:idents m.n.witnesses/idents}
   #?(:clj {:indexer q.n.witnesses/index-ids
            :counter q.n.witnesses/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.witnesses/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.witnesses/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.witnesses/id
   ao/pc-output [{::index [:total {:results [::m.n.witnesses/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr relay ::relay :ref
  {ao/target           ::m.n.relays/id
   ao/pc-input         #{::m.n.witnesses/id}
   ao/pc-output        [{::relay [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [_env props]
     (let [witness-id (::m.n.witnesses/id props)
           relay-id   #?(:clj (q.n.relays/find-by-witness witness-id)
                         :cljs (do (comment witness-id) nil))]
       {::relay (when relay-id (m.n.relays/ident relay-id))}))
   ::report/column-EQL {::relay [::m.n.relays/id ::m.n.relays/address]}})

(def attributes [admin-index index relay])
