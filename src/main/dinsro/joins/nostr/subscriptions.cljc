(ns dinsro.joins.nostr.subscriptions
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   #?(:clj [dinsro.queries.nostr.pubkeys :as q.n.pubkeys])
   #?(:clj [dinsro.queries.nostr.subscriptions :as q.n.subscriptions])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(def join-info
  (merge
   {:idents m.n.subscriptions/idents}
   #?(:clj {:indexer q.n.subscriptions/index-ids
            :counter q.n.subscriptions/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.subscriptions/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.subscriptions/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.subscriptions/id
   ao/pc-output [{::index [:total {:results [::m.n.subscriptions/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

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
