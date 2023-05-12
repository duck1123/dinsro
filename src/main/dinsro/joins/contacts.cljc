(ns dinsro.joins.contacts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.contacts :as m.contacts]
   #?(:clj [dinsro.queries.contacts :as q.contacts])
   [dinsro.specs]))

;; [[../model/contacts.cljc][Contacts Model]]
;; [[../ui/contacts.cljs][Contacts UI]]

(def join-info
  (merge
   {:idents m.contacts/idents}
   #?(:clj {:indexer q.contacts/index-ids
            :counter q.contacts/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target     ::m.contacts/id
   ao/pc-output  [{::admin-index [:total {:results [::m.contacts/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.contacts/id
   ao/pc-output [{::index [:total {:results [::m.contacts/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
