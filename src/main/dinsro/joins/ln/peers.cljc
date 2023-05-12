(ns dinsro.joins.ln.peers
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.ln.peers :as m.ln.peers]
   #?(:clj [dinsro.queries.ln.peers :as q.ln.peers])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.ln.peers/idents}
   #?(:clj {:indexer q.ln.peers/index-ids
            :counter q.ln.peers/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.ln.peers/id
   ao/pc-output [{::admin-index [:total {:results [::m.ln.peers/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.ln.peers/id
   ao/pc-output [{::index [:total {:result [::m.ln.peers/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
