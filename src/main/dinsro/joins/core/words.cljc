(ns dinsro.joins.core.words
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.words :as m.words]
   #?(:clj [dinsro.queries.core.words :as q.words])
   [dinsro.specs]))

(defattr index ::m.words/index :ref
  {ao/target    ::m.words/id
   ao/pc-output [{::m.words/index [::m.words/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.words/index-ids) :cljs [])]
       {::m.words/index (m.words/idents ids)}))})

(def attributes [index])
