(ns dinsro.joins.nostr.pubkeys
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   #?(:clj [dinsro.queries.nostr.events :as q.n.events])
   #?(:clj [dinsro.queries.nostr.pubkeys :as q.n.pubkeys])
   #?(:clj [dinsro.queries.nostr.subscriptions :as q.n.subscriptions])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/pubkeys.clj][Pubkey Actions]]
;; [[../../model/nostr/pubkeys.cljc][Pubkeys Model]]
;; [[../../mutations/nostr/pubkeys.cljc][Pubkey Mutations]]
;; [[../../queries/nostr/pubkeys.clj][Pubkey Queries]]
;; [[../../ui/nostr/pubkeys.cljs][Pubkeys UI]]

(defattr index ::index :ref
  {ao/target    ::m.n.pubkeys/id
   ao/pc-output [{::index [::m.n.pubkeys/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} params]
     (log/trace :index/starting {:query-params query-params :params params})
     (let [ids #?(:clj (q.n.pubkeys/index-ids) :cljs [])]
       {::index (m.n.pubkeys/idents ids)}))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.pubkeys/id
   ao/pc-output [{::admin-index [::m.n.pubkeys/id]}]
   ao/pc-resolve
   (fn [_env _]
     (log/info :admin-index/starting {})
     (let [ids #?(:clj (q.n.pubkeys/index-ids) :cljs [])]
       {::admin-index (m.n.pubkeys/idents ids)}))})

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

(defattr subscriptions ::subscriptions :ref
  {ao/identities #{::m.n.pubkeys/id}
   ao/pc-input   #{::m.n.pubkeys/id}
   ao/pc-output  [{::subscriptions [::m.n.subscriptions/id]}]
   ao/target     ::m.n.subscriptions/id
   ao/pc-resolve
   (fn [_env params]
     (if-let [pubkey-id (::m.n.pubkeys/id params)]
       (do
         (log/info :subscriptions/starting {:pubkey-id pubkey-id})
         (let [ids #?(:clj (q.n.subscriptions/find-by-pubkey pubkey-id)
                      :cljs (do (comment pubkey-id) []))]
           (log/trace :subscriptions/finished {:ids ids})
           {::subscriptions (m.n.subscriptions/idents ids)}))
       (throw (ex-info "No pubkey supplied" {}))))})

(defattr subscription-count ::subscription-count :int
  {ao/pc-input   #{::subscriptions}
   ao/pc-resolve (fn [_ {::keys [subscriptions]}] {::subscription-count (count subscriptions)})})

(defattr npub ::npub :string
  {ao/identities #{::m.n.pubkeys/hex}
   ao/pc-input   #{::m.n.pubkeys/id ::m.n.pubkeys/hex}
   ao/pc-resolve (fn [_env {::m.n.pubkeys/keys [hex]}]
                   {::npub (str "npubNOTCORRECT1"
                                ;; TODO: bech32
                                hex)})})

(def attributes [admin-index index
                 contact-count contacts
                 events event-count
                 subscription-count subscriptions
                 npub])
