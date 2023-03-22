(ns dinsro.joins.ln.peers
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln.peers :as m.ln.peers]
   #?(:clj [dinsro.queries.ln.peers :as q.ln.peers])
   [dinsro.specs]))

(defattr index ::index :ref
  {ao/target    ::m.ln.peers/id
   ao/pc-output [{::index [::m.ln.peers/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.ln.peers/index-ids) :cljs [])]
       {::index (m.ln.peers/idents ids)}))})

(def attributes [index])
