(ns dinsro.joins.nostr.badge-acceptances
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   #?(:clj [dinsro.queries.nostr.badge-acceptances :as q.n.badge-acceptances])))

(defattr index ::index :ref
  {ao/target    ::m.n.badge-acceptances/id
   ao/pc-output [{::index [::m.n.badge-acceptances/id]}]
   ao/pc-resolve
   (fn [_ _]
     (let [ids #?(:clj  (q.n.badge-acceptances/index-ids)
                  :cljs [])]
       {::index (m.n.badge-acceptances/idents ids)}))})

(def attributes [index])
