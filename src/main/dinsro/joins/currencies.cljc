(ns dinsro.joins.currencies
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [dinsro.queries.currencies :as q.currencies])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   #?(:clj [dinsro.queries.rate-sources :as q.rate-sources])
   #?(:clj [dinsro.queries.rates :as q.rates])
   [dinsro.specs]))

(defattr current-rate ::m.currencies/current-rate :ref
  {ao/cardinality :one
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::m.currencies/current-rate [::m.rates/id]}]
   ao/target      ::m.rates/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (let [id (if id #?(:clj (q.rates/find-top-by-currency id) :cljs []) [])]
       {::m.currencies/current-rate (m.rates/ident id)}))})

(defattr index ::m.currencies/index :ref
  {ao/target    ::m.currencies/id
   ao/pc-output [{::m.currencies/index [::m.currencies/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.currencies/index-ids) :cljs [])]
       {::m.currencies/index (m.currencies/idents ids)}))})

(defattr accounts ::m.currencies/accounts :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::m.currencies/accounts [::m.accounts/id]}]
   ao/target      ::m.accounts/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (let [ids (if id #?(:clj (q.accounts/find-by-currency id) :cljs []) [])]
       {::m.currencies/accounts (m.accounts/idents ids)}))})

(defattr rates ::m.currencies/rates :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::m.currencies/rates [::m.rates/id]}]
   ao/target      ::m.rates/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (let [ids (if id #?(:clj (q.rates/find-by-currency id) :cljs []) [])]
       {::m.currencies/rates (m.rates/idents ids)}))})

(defattr sources ::m.currencies/sources :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::m.currencies/sources [::m.rate-sources/id]}]
   ao/target      ::m.rate-sources/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (if id
       #?(:clj  (let [ids (q.rate-sources/index-ids-by-currency id)]
                  {::m.currencies/sources (map (fn [id] {::m.rate-sources/id id}) ids)})
          :cljs {::m.currencies/sources []})
       {::m.currencies/sources []}))})

(defattr transactions ::m.currencies/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::m.currencies/transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (let [ids (if id #?(:clj (q.transactions/find-by-currency id) :cljs []) [])]
       {::m.currencies/transactions (m.transactions/ident ids)}))})

(def attributes [current-rate index accounts rates sources transactions])
