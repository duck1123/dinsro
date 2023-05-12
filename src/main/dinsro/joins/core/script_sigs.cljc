(ns dinsro.joins.core.script-sigs
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.script-sigs :as m.c.script-sigs]
   #?(:clj [dinsro.queries.core.script-sigs :as q.c.script-sigs])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.c.script-sigs/idents}
   #?(:clj {:indexer q.c.script-sigs/index-ids
            :counter q.c.script-sigs/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.script-sigs/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.script-sigs/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.script-sigs/id
   ao/pc-output [{::index [:total {:result [::m.c.script-sigs/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
