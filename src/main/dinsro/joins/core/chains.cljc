(ns dinsro.joins.core.chains
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.chains :as m.c.chains]
   #?(:clj [dinsro.queries.core.chains :as q.c.chains])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.c.chains/idents}
   #?(:clj {:indexer q.c.chains/index-ids
            :counter q.c.chains/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.chains/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.chains/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.chains/id
   ao/pc-output [{::index [:total {:results [::m.c.chains/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
