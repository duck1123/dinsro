(ns dinsro.joins.core.tx
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   #?(:clj [dinsro.queries.core.nodes :as q.c.nodes])
   #?(:clj [dinsro.queries.core.tx :as q.c.tx])
   #?(:clj [dinsro.queries.core.tx-in :as q.c.tx-in])
   #?(:clj [dinsro.queries.core.tx-out :as q.c.tx-out])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.blocks/_)

(defattr index ::index :ref
  {ao/target    ::m.c.tx/id
   ao/pc-output [{::index [::m.c.tx/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :index/starting {:query-params query-params :props props})
     (let [ids
           #?(:clj
              (let [{block-id ::m.c.blocks/id} query-params]
                (if block-id
                  (q.c.tx/find-by-block block-id)
                  (q.c.tx/index-ids)))
              :cljs [])]
       {::index (m.c.tx/idents ids)}))})

(defattr node ::node :ref
  {ao/cardinality      :one
   ao/pc-input         #{::m.c.tx/id}
   ao/pc-output        [{::node [::m.c.nodes/id]}]
   ao/target           ::m.c.nodes/id
   ao/pc-resolve
   (fn [_env {::m.c.tx/keys [id]}]
     (let [node-id (if id #?(:clj (q.c.nodes/find-by-tx id) :cljs nil) nil)]
       {::node (m.c.nodes/ident node-id)}))
   ::report/column-EQL {::m.c.tx/node m.c.nodes/link-query}})

(defattr ins ::ins :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.tx/id}
   ao/pc-output   [{::ins [::m.c.tx-in/id]}]
   ao/target      ::m.c.tx-in/id
   ao/pc-resolve
   (fn [_env {::m.c.tx/keys [id]}]
     (let [ids (if id #?(:clj (q.c.tx-in/find-by-tx id) :cljs []) [])]
       {::ins (m.c.tx/idents ids)}))})

(defattr outs ::outs :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.tx/id}
   ao/pc-output   [{::outs [::m.c.tx-out/id]}]
   ao/target      ::m.c.tx-out/id
   ao/pc-resolve
   (fn [_env {::m.c.tx/keys [id]}]
     (let [ids (if id #?(:clj (q.c.tx-out/find-by-tx id) :cljs []) [])]
       {::outs (m.c.tx-out/idents ids)}))})

(def attributes [index node ins outs])
