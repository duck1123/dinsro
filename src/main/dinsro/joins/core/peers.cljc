(ns dinsro.joins.core.peers
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.peers :as m.c.peers]
   #?(:clj [dinsro.queries.core.peers :as q.c.peers])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.c.peers/idents}
   #?(:clj {:indexer q.c.peers/index-ids
            :counter q.c.peers/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.peers/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.peers/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.peers/id
   ao/pc-output [{::index [:total {:result [::m.c.peers/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
