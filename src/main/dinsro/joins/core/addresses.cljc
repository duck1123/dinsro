(ns dinsro.joins.core.addresses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.addresses :as m.c.addresses]
   #?(:clj [dinsro.queries.core.addresses :as q.c.addresses])
   [dinsro.specs]))

(defattr index ::index :ref
  {ao/target    ::m.c.addresses/id
   ao/pc-output [{::index [::m.c.addresses/id]}]
   ao/pc-resolve
   (fn [_ _]
     (let [ids #?(:clj (q.c.addresses/index-ids) :cljs [])]
       {::index (m.c.addresses/idents ids)}))})

(def attributes [index])
