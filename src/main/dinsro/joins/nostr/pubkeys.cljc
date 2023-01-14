(ns dinsro.joins.nostr.pubkeys
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   #?(:clj [dinsro.queries.nostr.pubkeys :as q.n.pubkeys])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::m.n.pubkeys/index :ref
  {ao/target    ::m.n.pubkeys/id
   ao/pc-output [{::m.n.pubkeys/index [::m.n.pubkeys/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.pubkeys/index-ids) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::m.n.pubkeys/index (m.n.pubkeys/idents ids)}))})

(defattr admin-index ::m.n.pubkeys/admin-index :ref
  {ao/target    ::m.n.pubkeys/id
   ao/pc-output [{::m.n.pubkeys/admin-index [::m.n.pubkeys/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.pubkeys/index-ids) :cljs [])]
       {::m.n.pubkeys/admin-index (m.n.pubkeys/idents ids)}))})

(def attributes
  [admin-index
   index])
