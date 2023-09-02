(ns dinsro.joins.core.blocks
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.transactions :as m.c.transactions]
   #?(:clj [dinsro.queries.core.blocks :as q.c.blocks])
   #?(:clj [dinsro.queries.core.transactions :as q.c.transactions])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.nodes/_ ::log/_)

(def model-key ::m.c.blocks/id)

(def join-info
  (merge
   {:idents m.c.blocks/idents}
   #?(:clj {:indexer q.c.blocks/index-ids
            :counter q.c.blocks/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.blocks/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.blocks/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.blocks/id
   ao/pc-output [{::index [:total {:results [::m.c.blocks/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr transactions ::transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.blocks/id}
   ao/pc-output   [{::transactions [::m.c.transactions/id]}]
   ao/target      ::m.c.transactions/id
   ao/pc-resolve
   (fn [_env {::m.c.blocks/keys [id]}]
     (let [ids (if id #?(:clj (q.c.transactions/index-ids {model-key id}) :cljs []) [])]
       {::transactions (m.c.transactions/idents ids)}))})

(def attributes [index admin-index transactions])
