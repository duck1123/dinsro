(ns dinsro.joins.nostr.subscription-pubkeys
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   #?(:clj [dinsro.queries.nostr.subscription-pubkeys :as q.n.subscription-pubkeys])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.n.subscription-pubkeys/idents}
   #?(:clj {:indexer q.n.subscription-pubkeys/index-ids
            :counter q.n.subscription-pubkeys/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.subscription-pubkeys/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.subscription-pubkeys/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    :m.n.subscription-pubkeys/id
   ao/pc-output [{::index [:total {:results [:m.n.subscription-pubkeys/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
