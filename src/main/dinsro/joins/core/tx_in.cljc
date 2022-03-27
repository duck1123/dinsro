(ns dinsro.joins.core.tx-in
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.tx-in :as m.core-tx-in]
   #?(:clj [dinsro.queries.core.tx-in :as q.core-tx-in])
   [dinsro.specs]))

(defattr index ::m.core-tx-in/index :ref
  {ao/target    ::m.core-tx-in/id
   ao/pc-output [{::m.core-tx-in/index [::m.core-tx-in/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.core-tx-in/index-ids) :cljs [])]
       {::m.core-tx-in/index (map (fn [id] {::m.core-tx-in/id id}) ids)}))})

(def attributes [index])
