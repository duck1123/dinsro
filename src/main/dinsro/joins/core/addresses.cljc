(ns dinsro.joins.core.addresses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.addresses :as m.core-addresses]
   #?(:clj [dinsro.queries.core.addresses :as q.core-addresses])
   [dinsro.specs]))

(defattr index ::m.core-addresses/index :ref
  {ao/target    ::m.core-addresses/id
   ao/pc-output [{::m.core-addresses/index [::m.core-addresses/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     (comment env query-params)
     (let [ids #?(:clj (q.core-addresses/index-ids) :cljs [])]
       {::m.core-addresses/index (m.core-addresses/idents ids)}))})

(def attributes
  [index])
