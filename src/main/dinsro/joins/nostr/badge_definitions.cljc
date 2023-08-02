(ns dinsro.joins.nostr.badge-definitions
  (:require
   #?(:clj [dinsro.queries.nostr.badge-definitions :as q.n.badge-definitions])
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]))

;; [[../../queries/nostr/badge_definitions.clj]]

(def join-info
  (merge
   {:idents m.n.badge-definitions/idents}
   #?(:clj {:indexer q.n.badge-definitions/index-ids
            :counter q.n.badge-definitions/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.badge-definitions/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.badge-definitions/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.badge-definitions/id
   ao/pc-output [{::index [:total {:results [::m.n.badge-definitions/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
