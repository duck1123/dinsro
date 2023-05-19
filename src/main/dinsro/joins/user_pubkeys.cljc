(ns dinsro.joins.user-pubkeys
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.user-pubkeys :as m.user-pubkeys]
   #?(:clj [dinsro.queries.user-pubkeys :as q.user-pubkeys])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.user-pubkeys/idents}
   #?(:clj {:indexer q.user-pubkeys/index-ids
            :counter q.user-pubkeys/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target     ::m.user-pubkeys/id
   ao/pc-output  [{::admin-index [:total {:results [::m.user-pubkeys/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.user-pubkeys/id
   ao/pc-output [{::index [:total {:results [::m.user-pubkeys/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
