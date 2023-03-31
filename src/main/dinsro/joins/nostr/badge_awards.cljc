(ns dinsro.joins.nostr.badge-awards
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   #?(:clj [dinsro.queries.nostr.badge-awards :as q.n.badge-awards])))

(defattr index ::index :ref
  {ao/target    ::m.n.badge-awards/id
   ao/pc-output [{::index [::m.n.badge-awards/id]}]
   ao/pc-resolve
   (fn [_ _]
     (let [ids #?(:clj  (q.n.badge-awards/index-ids)
                  :cljs [])]
       {::index (m.n.badge-awards/idents ids)}))})

(def attributes [index])
