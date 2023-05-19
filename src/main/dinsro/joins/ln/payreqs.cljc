(ns dinsro.joins.ln.payreqs
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   #?(:clj [dinsro.queries.ln.payreqs :as q.ln.payreqs])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.ln.payreqs/idents}
   #?(:clj {:indexer q.ln.payreqs/index-ids
            :counter q.ln.payreqs/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.ln.payreqs/id
   ao/pc-output [{::admin-index [:total {:results [::m.ln.payreqs/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.ln.payreqs/id
   ao/pc-output [{::index [:total {:results [::m.ln.payreqs/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
