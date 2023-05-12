(ns dinsro.joins.core.tx-in
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   #?(:clj [dinsro.queries.core.tx-in :as q.c.tx-in])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.c.tx-in/idents}
   #?(:clj {:indexer q.c.tx-in/index-ids
            :counter q.c.tx-in/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.tx-in/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.tx-in/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.tx-in/id
   ao/pc-output [{::index [:total {:result [::m.c.tx-in/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
