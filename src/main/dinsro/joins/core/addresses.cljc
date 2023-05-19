(ns dinsro.joins.core.addresses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.addresses :as m.c.addresses]
   #?(:clj [dinsro.queries.core.addresses :as q.c.addresses])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.c.addresses/idents}
   #?(:clj {:indexer q.c.addresses/index-ids
            :counter q.c.addresses/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.addresses/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.addresses/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.addresses/id
   ao/pc-output [{::index [:total {:results [::m.c.addresses/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
