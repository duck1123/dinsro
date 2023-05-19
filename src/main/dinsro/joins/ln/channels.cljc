(ns dinsro.joins.ln.channels
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.ln.channels :as m.ln.channels]
   #?(:clj [dinsro.queries.ln.channels :as q.ln.channels])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.ln.channels/idents}
   #?(:clj {:indexer q.ln.channels/index-ids
            :counter q.ln.channels/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.ln.channels/id
   ao/pc-output [{::admin-index [:total {:results [::m.ln.channels/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.ln.channels/id
   ao/pc-output [{::index [:total {:results [::m.ln.channels/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
