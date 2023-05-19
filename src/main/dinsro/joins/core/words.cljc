(ns dinsro.joins.core.words
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   #?(:clj [dinsro.queries.core.words :as q.c.words])
   [dinsro.specs]))

(comment ::m.c.wallets/_)

(def join-info
  (merge
   {:idents m.c.words/idents}
   #?(:clj {:indexer q.c.words/index-ids
            :counter q.c.words/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.words/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.words/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.words/id
   ao/pc-output [{::index [:total {:results [::m.c.words/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
