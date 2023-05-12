(ns dinsro.joins.core.transactions
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.joins :as j]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   #?(:clj [dinsro.queries.core.nodes :as q.c.nodes])
   #?(:clj [dinsro.queries.core.transactions :as q.c.transactions])
   #?(:clj [dinsro.queries.core.tx-in :as q.c.tx-in])
   #?(:clj [dinsro.queries.core.tx-out :as q.c.tx-out])
   [dinsro.specs]))

(comment ::m.c.blocks/_)

(def join-info
  (merge
   {:idents m.c.transactions/idents}
   #?(:clj {:indexer q.c.transactions/index-ids
            :counter q.c.transactions/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.transactions/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.transactions/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.transactions/id
   ao/pc-output [{::index [:total {:result [::m.c.transactions/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr node ::node :ref
  {ao/cardinality      :one
   ao/pc-input         #{::m.c.transactions/id}
   ao/pc-output        [{::node [::m.c.nodes/id]}]
   ao/target           ::m.c.nodes/id
   ao/pc-resolve
   (fn [_env {::m.c.transactions/keys [id]}]
     (let [node-id (if id #?(:clj (q.c.nodes/find-by-tx id) :cljs nil) nil)]
       {::node (m.c.nodes/ident node-id)}))
   ::report/column-EQL {::m.c.transactions/node m.c.nodes/link-query}})

(defattr ins ::ins :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.transactions/id}
   ao/pc-output   [{::ins [::m.c.tx-in/id]}]
   ao/target      ::m.c.tx-in/id
   ao/pc-resolve
   (fn [_env {::m.c.transactions/keys [id]}]
     (let [ids (if id #?(:clj (q.c.tx-in/find-by-tx id) :cljs []) [])]
       {::ins (m.c.transactions/idents ids)}))})

(defattr outs ::outs :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.transactions/id}
   ao/pc-output   [{::outs [::m.c.tx-out/id]}]
   ao/target      ::m.c.tx-out/id
   ao/pc-resolve
   (fn [_env {::m.c.transactions/keys [id]}]
     (let [ids (if id #?(:clj (q.c.tx-out/find-by-tx id) :cljs []) [])]
       {::outs (m.c.tx-out/idents ids)}))})

(def attributes [admin-index index node ins outs])
