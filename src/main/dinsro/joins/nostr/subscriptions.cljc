(ns dinsro.joins.nostr.subscriptions
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   #?(:clj [dinsro.queries.nostr.pubkeys :as q.n.pubkeys])
   #?(:clj [dinsro.queries.nostr.subscriptions :as q.n.subscriptions])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.n.subscriptions/id
   ao/pc-output [{::index [::m.n.subscriptions/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.subscriptions/index-ids) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::index (m.n.subscriptions/idents ids)}))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.subscriptions/id
   ao/pc-output [{::admin-index [::m.n.subscriptions/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.subscriptions/index-ids) :cljs [])]
       {::admin-index (m.n.subscriptions/idents ids)}))})

(defattr pubkey-count ::pubkey-count :int
  {ao/identities #{::m.n.subscriptions/id}
   ao/pc-input   #{::m.n.subscriptions/id}
   ao/pc-resolve
   (fn [_env params]
     (log/info :pubkey-count/starting {:params params})
     (let [subscription-id (::m.n.subscriptions/id params)
           pubkeys         #?(:clj (q.n.pubkeys/find-by-subscription subscription-id)
                              :cljs
                              (do
                                (comment subscription-id)
                                []))]
       {::pubkey-count (count pubkeys)}))})

(def attributes [admin-index index pubkey-count])
