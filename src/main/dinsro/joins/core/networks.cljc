(ns dinsro.joins.core.networks
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.networks :as m.c.networks]
   #?(:clj [dinsro.queries.core.networks :as q.c.networks])
   [dinsro.specs]))

(defattr index ::m.c.networks/index :ref
  {ao/target    ::m.c.networks/id
   ao/pc-output [{::m.c.networks/index [::m.c.networks/id]}]
   ao/pc-resolve
   (fn [_env _props]
     (let [ids #?(:clj (q.c.networks/index-ids) :cljs [])]
       {::m.c.networks/index (map (fn [id] {::m.c.networks/id id}) ids)}))})

(def attributes [index])
