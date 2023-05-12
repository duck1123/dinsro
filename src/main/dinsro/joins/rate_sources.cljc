(ns dinsro.joins.rate-sources
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   #?(:clj [dinsro.queries.rates :as q.rates])
   #?(:clj [dinsro.queries.rate-sources :as q.rate-sources])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.rate-sources/idents}
   #?(:clj {:indexer q.rate-sources/index-ids
            :counter q.rate-sources/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target     ::m.rate-sources/id
   ao/pc-output  [{::admin-index [:total {:results [::m.rate-sources/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.rate-sources/id
   ao/pc-output [{::index [:total {:results [::m.rate-sources/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr current-rate ::current-rate :ref
  {ao/cardinality :one
   ao/target      ::m.rates/id
   ao/pc-input    #{::m.rate-sources/id}
   ao/pc-output   [{::current-rate [::m.rates/id]}]
   ao/pc-resolve
   (fn [_env {::m.rate-sources/keys [id]}]
     (let [id (when id #?(:clj (q.rates/find-top-by-rate-source id) :cljs nil))]
       {::current-rate (when id (m.rates/ident id))}))})

(defattr rates ::rates :ref
  {ao/cardinality :many
   ao/target      ::m.rates/id
   ao/pc-input    #{::m.rate-sources/id}
   ao/pc-output   [{::rates [::m.rates/id]}]
   ao/pc-resolve
   (fn [_env {::m.rate-sources/keys [id]}]
     (if id
       (let [ids #?(:clj (q.rates/find-by-rate-source id) :cljs [])]
         {::rates (m.rates/idents ids)})
       {:errors "no id"}))})

(defattr rate-count ::rate-count :ref
  {ao/cardinality :many
   ao/target      ::m.rates/id
   ao/pc-input    #{::m.rate-sources/id}
   ao/pc-output   [{::rate-count [::m.rates/id]}]
   ao/pc-resolve
   (fn [_env {::m.rate-sources/keys [id]}]
     (if id
       (let [ids #?(:clj (q.rates/find-by-rate-source id) :cljs [])]
         {::rate-count (count ids)})
       {:errors "no id"}))})

(def attributes [admin-index current-rate index rates rate-count])
