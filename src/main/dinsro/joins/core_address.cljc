(ns dinsro.joins.core-address
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core-address :as m.core-address]
   #?(:clj [dinsro.queries.core-address :as q.core-address])
   [dinsro.specs]))

(defattr index ::m.core-address/index :ref
  {ao/target    ::m.core-address/id
   ao/pc-output [{::m.core-address/index [::m.core-address/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     (comment env query-params)
     (let [ids #?(:clj (q.core-address/index-ids) :cljs [])]
       {::m.core-address/index (m.core-address/idents ids)}))})

(def attributes
  [index])
