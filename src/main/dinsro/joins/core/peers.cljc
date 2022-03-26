(ns dinsro.joins.core-peers
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core-peers :as m.core-peers]
   #?(:clj [dinsro.queries.core-peers :as q.core-peers])
   [dinsro.specs]))

(defattr index ::m.core-peers/index :ref
  {ao/target    ::m.core-peers/id
   ao/pc-output [{::m.core-peers/index [::m.core-peers/id]}]
   ao/pc-resolve
   (fn [_env _props]
     (let [ids #?(:clj (q.core-peers/index-ids) :cljs [])]
       {::m.core-peers/index (map (fn [id] {::m.core-peers/id id}) ids)}))})

(def attributes [index])
