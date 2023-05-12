(ns dinsro.joins.rates
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   #?(:clj [dinsro.queries.rates :as q.rates])))

;; [../actions/rates.clj]
;; [../model/rates.cljc]
;; [../queries/rates.clj]
;; [../ui/rates.cljs]

(comment ::m.rate-sources/_)

(def join-info
  (merge
   {:idents m.rates/idents}
   #?(:clj {:indexer q.rates/index-ids
            :counter q.rates/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target     ::m.rates/id
   ao/pc-output  [{::admin-index [:total {:results [::m.rates/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.rates/id
   ao/pc-output [{::index [:total {:results [::m.rates/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
