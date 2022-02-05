(ns dinsro.joins.core-tx
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.core-tx :as m.core-tx]
   [dinsro.model.core-tx-in :as m.core-tx-in]
   [dinsro.model.core-tx-out :as m.core-tx-out]
   #?(:clj [dinsro.queries.core-nodes :as q.core-nodes])
   #?(:clj [dinsro.queries.core-tx :as q.core-tx])
   #?(:clj [dinsro.queries.core-tx-in :as q.core-tx-in])
   #?(:clj [dinsro.queries.core-tx-out :as q.core-tx-out])
   [dinsro.specs]))

(defattr index ::m.core-tx/index :ref
  {ao/target    ::m.core-tx/id
   ao/pc-output [{::m.core-tx/index [::m.core-tx/id]}]
   ao/pc-resolve
   (fn [_env _props]
     (let [ids #?(:clj (q.core-tx/index-ids) :cljs [])]
       {::m.core-tx/index (map (fn [id] {::m.core-tx/id id}) ids)}))})

(defattr node ::m.core-tx/node :ref
  {ao/cardinality      :one
   ao/pc-input         #{::m.core-tx/id}
   ao/pc-output        [{::m.core-tx/node [::m.core-nodes/id]}]
   ao/target           ::m.core-nodes/id
   ao/pc-resolve
   (fn [_env {::m.core-tx/keys [id]}]
     (let [node-id (if id #?(:clj (q.core-nodes/find-by-tx id) :cljs nil) nil)]
       {::m.core-tx/node (m.core-nodes/ident node-id)}))
   ::report/column-EQL {::m.core-tx/node m.core-nodes/link-query}})

(defattr ins ::m.core-tx/ins :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.core-tx/id}
   ao/pc-output   [{::m.core-tx/ins [::m.core-tx-in/id]}]
   ao/target      ::m.core-tx-in/id
   ao/pc-resolve
   (fn [_env {::m.core-tx/keys [id]}]
     (let [ids (if id #?(:clj (q.core-tx-in/find-by-tx id) :cljs []) [])]
       {::m.core-tx/ins (m.core-tx/idents ids)}))})

(defattr outs ::m.core-tx/outs :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.core-tx/id}
   ao/pc-output   [{::m.core-tx/outs [::m.core-tx-out/id]}]
   ao/target      ::m.core-tx-out/id
   ao/pc-resolve
   (fn [_env {::m.core-tx/keys [id]}]
     (let [ids (if id #?(:clj (q.core-tx-out/find-by-tx id) :cljs []) [])]
       {::m.core-tx/outs (m.core-tx-out/idents ids)}))})

(def attributes [index node ins outs])
