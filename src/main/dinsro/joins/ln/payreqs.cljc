(ns dinsro.joins.ln.payreqs
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   #?(:clj [dinsro.queries.ln.payreqs :as q.ln.payreqs])
   [dinsro.specs]))

(defattr index ::index :ref
  {ao/target    ::m.ln.payreqs/id
   ao/pc-output [{::index [::m.ln.payreqs/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.ln.payreqs/index-ids) :cljs [])]
       {::index (m.ln.payreqs/idents ids)}))})

(def attributes [index])
