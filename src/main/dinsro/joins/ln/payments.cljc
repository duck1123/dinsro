(ns dinsro.joins.ln.payments
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.ln.payments :as m.ln.payments]
   #?(:clj [dinsro.queries.ln.payments :as q.ln.payments])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.ln.payments/idents}
   #?(:clj {:indexer q.ln.payments/index-ids
            :counter q.ln.payments/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.ln.payments/id
   ao/pc-output [{::admin-index [:total {:results [::m.ln.payments/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.ln.payments/id
   ao/pc-output [{::index [:total {:results [::m.ln.payments/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
