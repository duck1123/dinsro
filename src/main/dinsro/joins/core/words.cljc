(ns dinsro.joins.core.words
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.words :as m.c.words]
   #?(:clj [dinsro.queries.core.words :as q.c.words])
   [dinsro.specs]))

(defattr index ::m.c.words/index :ref
  {ao/target    ::m.c.words/id
   ao/pc-output [{::m.c.words/index [::m.c.words/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.c.words/index-ids) :cljs [])]
       {::m.c.words/index (m.c.words/idents ids)}))})

(def attributes [index])
