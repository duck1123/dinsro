(ns dinsro.joins.nostr.badge-acceptances
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   #?(:clj [dinsro.queries.nostr.badge-acceptances :as q.n.badge-acceptances])))

(def join-info
  (merge
   {:idents m.n.badge-acceptances/idents}
   #?(:clj {:indexer q.n.badge-acceptances/index-ids
            :counter q.n.badge-acceptances/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.badge-acceptances/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.badge-acceptances/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.badge-acceptances/id
   ao/pc-output [{::index [:total {:result [::m.n.badge-acceptances/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
