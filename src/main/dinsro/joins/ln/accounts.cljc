(ns dinsro.joins.ln.accounts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   #?(:clj [dinsro.queries.ln.accounts :as q.ln.accounts])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.ln.accounts/idents}
   #?(:clj {:indexer q.ln.accounts/index-ids
            :counter q.ln.accounts/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.ln.accounts/id
   ao/pc-output [{::admin-index [:total {:results [::m.ln.accounts/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.ln.accounts/id
   ao/pc-output [{::index [:total {:results [::m.ln.accounts/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
