(ns dinsro.joins.core.networks
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   #?(:clj [dinsro.queries.core.networks :as q.c.networks])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.c.chains/idents}
   #?(:clj {:indexer q.c.networks/index-ids
            :counter q.c.networks/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.networks/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.networks/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.networks/id
   ao/pc-output [{::index [:total {:result [::m.c.networks/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
