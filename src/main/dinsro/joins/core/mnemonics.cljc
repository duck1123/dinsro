(ns dinsro.joins.core.mnemonics
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]
   #?(:clj [dinsro.queries.core.mnemonics :as q.c.mnemonics])))

(def join-info
  (merge
   {:idents m.c.mnemonics/idents}
   #?(:clj {:indexer q.c.mnemonics/index-ids
            :counter q.c.mnemonics/count-ids})))

(defattr admin-index
  ::admin-index :ref
  {ao/target    ::m.c.mnemonics/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.mnemonics/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.mnemonics/id
   ao/pc-output [{::index [:total {:results [::m.c.mnemonics/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
