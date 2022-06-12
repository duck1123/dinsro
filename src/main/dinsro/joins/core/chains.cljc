(ns dinsro.joins.core.chains
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.chains :as m.c.chains]
   #?(:clj [dinsro.queries.core.chains :as q.c.chains])
   [dinsro.specs]))

(defattr index ::m.c.chains/index :ref
  {ao/target    ::m.c.chains/id
   ao/pc-output [{::m.c.chains/index [::m.c.chains/id]}]
   ao/pc-resolve
   (fn [_env _props]
     (let [ids #?(:clj (q.c.chains/index-ids) :cljs [])]
       {::m.c.chains/index (map (fn [id] {::m.c.chains/id id}) ids)}))})

(def attributes [index])
