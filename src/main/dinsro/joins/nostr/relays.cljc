(ns dinsro.joins.nostr.relays
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.relays :as m.n.relays]
   #?(:clj [dinsro.queries.nostr.relays :as q.n.relays])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::m.n.relays/index :ref
  {ao/target    ::m.n.relays/id
   ao/pc-output [{::m.n.relays/index [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.relays/index-ids) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::m.n.relays/index (m.n.relays/idents ids)}))})

(defattr admin-index ::m.n.relays/admin-index :ref
  {ao/target    ::m.n.relays/id
   ao/pc-output [{::m.n.relays/admin-index [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.relays/index-ids) :cljs [])]
       {::m.n.relays/admin-index (m.n.relays/idents ids)}))})

(def attributes [admin-index index])
