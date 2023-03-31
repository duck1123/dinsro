(ns dinsro.joins.nostr.badge-definitions
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   #?(:clj [dinsro.queries.nostr.badge-definitions :as q.n.badge-definitions])))

(defattr index ::index :ref
  {ao/target    ::m.n.badge-definitions/id
   ao/pc-output [{::index [::m.n.badge-definitions/id]}]
   ao/pc-resolve
   (fn [_ _]
     (let [ids #?(:clj  (q.n.badge-definitions/index-ids)
                  :cljs [])]
       {::index (m.n.badge-definitions/idents ids)}))})

(def attributes [index])
