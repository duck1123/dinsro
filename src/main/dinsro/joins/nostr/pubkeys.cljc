(ns dinsro.joins.nostr.pubkeys
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.actions.nostr.pubkeys :as a.n.pubkeys])
   [dinsro.joins :as j]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   #?(:clj [dinsro.queries.nostr.events :as q.n.events])
   #?(:clj [dinsro.queries.nostr.pubkeys :as q.n.pubkeys])
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/pubkeys.clj]]
;; [[../../model/nostr/pubkeys.cljc]]
;; [[../../mutations/nostr/pubkeys.cljc]]
;; [[../../queries/nostr/pubkeys.clj]]
;; [[../../ui/nostr/pubkeys.cljs]]

(def join-info
  (merge
   {:idents m.n.pubkeys/idents}
   #?(:clj {:indexer q.n.pubkeys/index-ids
            :counter q.n.pubkeys/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.pubkeys/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.pubkeys/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.pubkeys/id
   ao/pc-output [{::index [:total {:results [::m.n.pubkeys/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr contacts ::contacts :ref
  {ao/identities #{::m.n.pubkeys/id}
   ao/target     ::m.contacts/id
   ao/pc-input   #{::m.n.pubkeys/id}
   ao/pc-output  [{::contacts [::m.contacts/id]}]
   ao/pc-resolve
   (fn [_ params]
     (if-let [pubkey-id (::m.n.pubkeys/id params)]
       (do
         (log/info :contacts/starting {:pubkey-id pubkey-id})
         (let [ids #?(:clj  (q.n.pubkeys/find-contacts pubkey-id)
                      :cljs (do (comment pubkey-id) []))]
           {::contacts (m.n.pubkeys/idents ids)}))
       (throw (ex-info "No pubkey supplied" {}))))})

(defattr contact-count ::contact-count :int
  {ao/pc-input   #{::contacts}
   ao/pc-resolve (fn [_ {::keys [contacts]}] {::contact-count (count contacts)})})

(defattr events ::events :ref
  {ao/identities #{::m.n.pubkeys/id}
   ao/target     ::m.n.pubkeys/id
   ao/pc-input   #{::m.n.pubkeys/id}
   ao/pc-output  [{::events [::m.n.events/id]}]
   ao/pc-resolve
   (fn [_ params]
     (if-let [pubkey-id (::m.n.pubkeys/id params)]
       (do
         (log/info :events/starting {:pubkey-id pubkey-id})
         (let [ids #?(:clj  (q.n.events/find-by-author pubkey-id)
                      :cljs (do (comment pubkey-id) []))]
           {::events (m.n.events/idents ids)}))
       (throw (ex-info "No pubkey supplied" {}))))})

(defattr event-count ::event-count :int
  {ao/pc-input   #{::events}
   ao/pc-resolve (fn [_ {::keys [events]}] {::event-count (count events)})})

(defattr npub ::npub :string
  {ao/pc-input   #{::m.n.pubkeys/id ::m.n.pubkeys/hex}
   ao/pc-resolve (fn [_env {::m.n.pubkeys/keys [hex]}]
                   {::npub #?(:clj (a.n.pubkeys/calculate-npub hex)
                              :cljs (do (comment hex) "npub1NOTIMPLEMENTED"))})})

(def attributes [admin-index index
                 contact-count contacts
                 events event-count
                 npub])
