(ns dinsro.joins.nostr.runs
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.joins :as j]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.runs :as m.n.runs]
   #?(:clj [dinsro.queries.nostr.relays :as q.n.relays])
   #?(:clj [dinsro.queries.nostr.runs :as q.n.runs])
   [dinsro.specs]))

;; [[../../model/nostr/runs.cljc]]
;; [[../../queries/nostr/runs.clj]]

(def join-info
  (merge
   {:idents m.n.runs/idents}
   #?(:clj {:indexer q.n.runs/index-ids
            :counter q.n.runs/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.runs/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.runs/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.runs/id
   ao/pc-output [{::index [:total {:results [::m.n.runs/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr relay ::relay :ref
  {ao/target           ::m.n.relays/id
   ao/pc-input         #{::m.n.runs/id}
   ao/pc-output        [{::relay [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [_env props]
     (let [run-id (::m.n.runs/id props)
           relay-id   #?(:clj (q.n.relays/find-by-run run-id)
                         :cljs (do (comment run-id) nil))]
       {::relay (when relay-id (m.n.relays/ident relay-id))}))
   ::report/column-EQL {::relay [::m.n.relays/id ::m.n.relays/address]}})

(def attributes [admin-index index relay])
