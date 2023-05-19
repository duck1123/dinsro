(ns dinsro.joins.ln.invoices
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   #?(:clj [dinsro.queries.ln.invoices :as q.ln.invoices])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.ln.invoices/idents}
   #?(:clj {:indexer q.ln.invoices/index-ids
            :counter q.ln.invoices/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.ln.invoices/id
   ao/pc-output [{::admin-index [:total {:results [::m.ln.invoices/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.ln.invoices/id
   ao/pc-output [{::index [:total {:results [::m.ln.invoices/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
