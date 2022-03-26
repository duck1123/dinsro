(ns dinsro.joins.core-block
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core-block :as m.core-block]
   [dinsro.model.core-tx :as m.core-tx]
   #?(:clj [dinsro.queries.core-block :as q.core-block])
   #?(:clj [dinsro.queries.core-tx :as q.core-tx])
   [dinsro.specs]))

(defattr index ::m.core-block/index :ref
  {ao/target    ::m.core-block/id
   ao/pc-output [{::m.core-block/index [::m.core-block/id]}]
   ao/pc-resolve
   (fn [_env _props]
     (let [ids #?(:clj (q.core-block/index-ids) :cljs [])]
       {::m.core-block/index (m.core-block/idents ids)}))})

(defattr transactions ::m.core-block/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.core-block/id}
   ao/pc-output   [{::m.core-block/transactions [::m.core-tx/id]}]
   ao/target      ::m.core-tx/id
   ao/pc-resolve
   (fn [_env {::m.core-block/keys [id]}]
     (let [ids (if id #?(:clj (q.core-tx/find-by-block id) :cljs []) [])]
       {::m.core-block/transactions (m.core-tx/idents ids)}))})

(def attributes
  [index
   transactions])
