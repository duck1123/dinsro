(ns dinsro.joins.rate-sources
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   #?(:clj [dinsro.queries.rates :as q.rates])
   #?(:clj [dinsro.queries.rate-sources :as q.rate-sources])
   [dinsro.specs]))

(defattr current-rate ::m.rate-sources/current-rate :ref
  {ao/cardinality :one
   ao/target      ::m.rates/id
   ao/pc-input    #{::m.rate-sources/id}
   ao/pc-output   [{::m.rate-sources/current-rate [::m.rates/id]}]
   ao/pc-resolve
   (fn [_env {::m.rate-sources/keys [id]}]
     (let [id (when id #?(:clj (q.rates/find-top-by-rate-source id) :cljs nil))]
       {::m.rate-sources/current-rate (when id (m.rates/ident id))}))})

(defattr index ::m.rate-sources/index :ref
  {ao/target    ::m.rate-sources/id
   ao/pc-output [{::m.rate-sources/index [::m.rate-sources/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     (let [ids #?(:clj (q.rate-sources/index-ids) :cljs [])]
       (comment env query-params)
       {::m.rate-sources/index (m.rate-sources/idents ids)}))})

(defattr rates ::m.rate-sources/rates :ref
  {ao/cardinality :many
   ao/target      ::m.rates/id
   ao/pc-input    #{::m.rate-sources/id}
   ao/pc-output   [{::m.rate-sources/rates [::m.rates/id]}]
   ao/pc-resolve
   (fn [_env {::m.rate-sources/keys [id]}]
     (if id
       #?(:clj (let [rate-ids (q.rates/find-by-rate-source id)]
                 {::m.rate-sources/rates (map (fn [id] {::m.rates/id id}) rate-ids)})
          :cljs {::m.rate-sources/rates []})
       {:errors "no id"}))})

(def attributes [current-rate index rates])
