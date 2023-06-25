(ns dinsro.joins.categories
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.transactions :as m.transactions]
   #?(:clj [dinsro.queries.categories :as q.categories])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   [dinsro.specs]))

;; [../actions/categories.clj]
;; [../model/categories.cljc]
;; [../mutations/categories.cljc]
;; [../queries/categories.clj]

(def join-info
  (merge
   {:idents m.categories/idents}
   #?(:clj {:indexer q.categories/index-ids
            :counter q.categories/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.categories/id
   ao/pc-output [{::admin-index [:total {:results [::m.categories/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.categories/id
   ao/pc-output [{::index [:total {:results [::m.categories/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(>def ::transactions (s/coll-of (s/keys :req [::m.transactions/id])))
(defattr transactions ::transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.categories/id}
   ao/pc-output   [{::transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.categories/keys [id]}]
     (let [ids (if id #?(:clj (q.transactions/find-by-category id) :cljs []) [])]
       {::transactions (m.categories/idents ids)}))})

(>def ::transaction-count number?)
(defattr transaction-count ::transaction-count :number
  {ao/pc-input   #{::transactions}
   ao/pc-resolve (fn [_ {::keys [transactions]}] {::transaction-count (count transactions)})})

(def attributes
  [admin-index
   index
   transaction-count
   transactions])
