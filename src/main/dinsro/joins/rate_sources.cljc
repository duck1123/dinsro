(ns dinsro.joins.rate-sources
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   #?(:clj [dinsro.queries.rates :as q.rates])
   #?(:clj [dinsro.queries.rate-sources :as q.rate-sources])
   [dinsro.specs]))

(defattr current-rate ::current-rate :ref
  {ao/cardinality :one
   ao/target      ::m.rates/id
   ao/pc-input    #{::m.rate-sources/id}
   ao/pc-output   [{::current-rate [::m.rates/id]}]
   ao/pc-resolve
   (fn [_env {::m.rate-sources/keys [id]}]
     (let [id (when id #?(:clj (q.rates/find-top-by-rate-source id) :cljs nil))]
       {::current-rate (when id (m.rates/ident id))}))})

(defattr index ::index :ref
  {ao/target    ::m.rate-sources/id
   ao/pc-output [{::index [::m.rate-sources/id]}]
   ao/pc-resolve
   (fn [_ _]
     (let [ids #?(:clj (q.rate-sources/index-ids) :cljs [])]
       {::index (m.rate-sources/idents ids)}))})

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

(def attributes [current-rate index rates])
