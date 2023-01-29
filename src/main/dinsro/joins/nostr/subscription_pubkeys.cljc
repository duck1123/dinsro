(ns dinsro.joins.nostr.subscription-pubkeys
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   #?(:clj [dinsro.queries.nostr.subscription-pubkeys :as q.n.subscription-pubkeys])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.n.subscription-pubkeys/id
   ao/pc-output [{::index [::m.n.subscription-pubkeys/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.subscription-pubkeys/index-ids) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::index (m.n.subscription-pubkeys/idents ids)}))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.subscription-pubkeys/id
   ao/pc-output [{::admin-index [::m.n.subscription-pubkeys/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.subscription-pubkeys/index-ids) :cljs [])]
       {::admin-index (m.n.subscription-pubkeys/idents ids)}))})

(def attributes [admin-index index])
