(ns dinsro.joins.currencies
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [dinsro.queries.currencies :as q.currencies])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   #?(:clj [dinsro.queries.rate-sources :as q.rate-sources])
   #?(:clj [dinsro.queries.rates :as q.rates])
   [dinsro.specs]))

(def model-key ::m.currencies/id)

(def join-info
  (merge
   {:idents m.currencies/idents}
   #?(:clj {:indexer q.currencies/index-ids
            :counter q.currencies/count-ids})))

(defattr admin-flat-index ::admin-flat-index :ref
  {ao/target    model-key
   ao/pc-output [{::admin-flat-index [model-key]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-flat-index (:results (j/make-admin-indexer join-info env props))})})

(defattr admin-index ::admin-index :ref
  {ao/target     ::m.currencies/id
   ao/pc-output  [{::admin-index [:total {:results [::m.currencies/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr flat-index ::flat-index :ref
  {ao/target    model-key
   ao/pc-output [{::flat-index [model-key]}]
   ao/pc-resolve
   (fn [env props]
     {::flat-index (j/make-flat-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.currencies/id
   ao/pc-output [{::index [:total {:results [::m.currencies/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr current-rate ::current-rate :ref
  {ao/cardinality :one
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::current-rate [::m.rates/id]}]
   ao/target      ::m.rates/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (let [id (if id #?(:clj (q.rates/find-top-by-currency id) :cljs []) [])]
       {::current-rate (m.rates/ident id)}))})

(defattr account-count ::account-count :number
  {ao/pc-input    #{::accounts}
   ao/pc-resolve  (fn [_ {::keys [accounts]}] {::account-count (count accounts)})})

(defattr accounts ::accounts :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::accounts [::m.accounts/id]}]
   ao/target      ::m.accounts/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (let [ids (if id #?(:clj (q.accounts/index-ids {model-key id}) :cljs []) [])]
       {::accounts (m.accounts/idents ids)}))})

(defattr rates ::rates :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::rates [::m.rates/id]}]
   ao/target      ::m.rates/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (let [ids (if id #?(:clj (q.rates/index-ids {model-key id}) :cljs []) [])]
       {::rates (m.rates/idents ids)}))})

(defattr rate-count ::rate-count :number
  {ao/pc-input    #{::rates}
   ao/pc-resolve  (fn [_ {::keys [rates]}] {::rate-count (count rates)})})

(defattr sources ::sources :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::sources [::m.rate-sources/id]}]
   ao/target      ::m.rate-sources/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (let [ids (if id  #?(:clj (q.rate-sources/index-ids {model-key id}) :cljs []) [])]
       {::sources (m.rate-sources/idents ids)}))})

(defattr source-count ::source-count :number
  {ao/pc-input    #{::sources}
   ao/pc-resolve  (fn [_ {::keys [sources]}] {::source-count (count sources)})})

(defattr transactions ::transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (let [ids (if id #?(:clj (q.transactions/index-ids {model-key id}) :cljs []) [])]
       {::transactions (m.transactions/ident ids)}))})

(defattr transaction-count ::transaction-count :number
  {ao/pc-input    #{::transactions}
   ao/pc-resolve  (fn [_ {::keys [transactions]}] {::transaction-count (count transactions)})})

(def attributes
  [account-count accounts
   admin-flat-index
   admin-index
   current-rate
   flat-index
   index
   rate-count rates
   transactions transaction-count
   source-count sources])
