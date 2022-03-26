(ns dinsro.joins.core-tx-out
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core-tx-out :as m.core-tx-out]
   #?(:clj [dinsro.queries.core-tx-out :as q.core-tx-out])
   [dinsro.specs]))

(defattr index ::m.core-tx-out/index :ref
  {ao/target    ::m.core-tx-out/id
   ao/pc-output [{::m.core-tx-out/index [::m.core-tx-out/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.core-tx-out/index-ids) :cljs [])]
       {::m.core-tx-out/index (map (fn [id] {::m.core-tx-out/id id}) ids)}))})

(def attributes [index])
