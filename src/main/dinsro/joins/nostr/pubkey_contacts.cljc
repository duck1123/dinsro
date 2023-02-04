(ns dinsro.joins.nostr.pubkey-contacts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.pubkey-contacts :as m.n.pubkey-contacts]
   #?(:clj [dinsro.queries.nostr.pubkey-contacts :as q.n.pubkey-contacts])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/pubkey_contacts.clj][Pubkey Contact Actions]]
;; [[../../model/nostr/pubkey_contacts.cljc][Pubkey Contacts Model]]


(defattr index ::index :ref
  {ao/target    ::m.n.pubkey-contacts/id
   ao/pc-output [{::index [::m.n.pubkey-contacts/id]}]
   ao/pc-resolve
   (fn [_env _]
     (log/info :index/starting {})
     (let [ids #?(:clj (q.n.pubkey-contacts/index-ids) :cljs [])]
       {::index (m.n.pubkey-contacts/idents ids)}))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.pubkey-contacts/id
   ao/pc-output [{::admin-index [::m.n.pubkey-contacts/id]}]
   ao/pc-resolve
   (fn [_env _]
     (log/info :admin-index/starting {})
     (let [ids #?(:clj (q.n.pubkey-contacts/index-ids) :cljs [])]
       {::admin-index (m.n.pubkey-contacts/idents ids)}))})

(def attributes [admin-index index])
