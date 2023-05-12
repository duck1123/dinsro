(ns dinsro.joins.nostr.pubkey-contacts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.nostr.pubkey-contacts :as m.n.pubkey-contacts]
   #?(:clj [dinsro.queries.nostr.pubkey-contacts :as q.n.pubkey-contacts])
   [dinsro.specs]))

;; [../../actions/nostr/pubkey_contacts.clj]
;; [../../model/nostr/pubkey_contacts.cljc]

(def join-info
  (merge
   {:idents m.n.pubkey-contacts/idents}
   #?(:clj {:indexer q.n.pubkey-contacts/index-ids
            :counter q.n.pubkey-contacts/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.pubkey-contacts/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.pubkey-contacts/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.pubkey-contacts/id
   ao/pc-output [{::index [:total {:results [::m.n.pubkey-contacts/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
