(ns dinsro.joins.core.script-sigs
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.script-sigs :as m.c.script-sigs]
   #?(:clj [dinsro.queries.core.script-sigs :as q.c.script-sigs])
   [dinsro.specs]))

(defattr index ::index :ref
  {ao/target    ::m.c.script-sigs/id
   ao/pc-output [{::index [::m.c.script-sigs/id]}]
   ao/pc-resolve
   (fn [_ _]
     (let [ids #?(:clj (q.c.script-sigs/index-ids) :cljs [])]
       {::index (m.c.script-sigs/idents ids)}))})

(def attributes [index])
