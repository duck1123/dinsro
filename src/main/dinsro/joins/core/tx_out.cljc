(ns dinsro.joins.core.tx-out
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   #?(:clj [dinsro.queries.core.tx-out :as q.c.tx-out])
   [dinsro.specs]))

(comment ::m.c.transactions/_)

(def join-info
  (merge
   {:idents m.c.tx-out/idents}
   #?(:clj {:indexer q.c.tx-out/index-ids
            :counter q.c.tx-out/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.tx-out/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.tx-out/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.tx-out/id
   ao/pc-output [{::index [:total {:results [::m.c.tx-out/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
