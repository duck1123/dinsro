(ns dinsro.actions.nostr.pubkey-contacts
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.nostr.pubkeys :as a.n.pubkeys]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.pubkey-contacts :as m.n.pubkey-contacts]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.queries.nostr.pubkey-contacts :as q.n.pubkey-contacts]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/pubkey_contacts.cljc][Pubkey Contacts Model]]
;; [[../../mutations/nostr/pubkey_contacts.cljc][Pubkey Contact Mutations]]
;; [[../../queries/nostr/pubkey_contacts.clj][Pubkey Contact Queries]]
;; [[../../ui/nostr/pubkey_contacts.cljs][Pubkey Contacts UI]]

(defn register-contact!
  [actor-id target-id]
  (if-let [contact-id (q.n.pubkey-contacts/find-by-actor-and-target actor-id target-id)]
    contact-id
    (let [params {::m.n.pubkey-contacts/actor actor-id ::m.n.pubkey-contacts/target target-id}]
      (q.n.pubkey-contacts/create-record params))))

(>defn fetch-contacts!
  ([pubkey-id]
   [::m.n.pubkeys/id => any?]
   (let [relay-id (first (q.n.relays/index-ids))]
     (fetch-contacts! pubkey-id relay-id)))
  ([pubkey-id relay-id]
   [::m.n.pubkeys/id ::m.n.relays/id => any?]
   (do
     (log/info :fetch-contacts!/starting {:pubkey-id pubkey-id :relay-id relay-id})
     (if-let [pubkey (q.n.pubkeys/read-record pubkey-id)]
       (let [pubkey-hex (::m.n.pubkeys/hex pubkey)
             body       {:authors [pubkey-hex] :kinds [3]}
             ch         (a.n.relays/send! relay-id body)]
         (log/info :fetch-contact!/sent {:ch ch})
         (async/go-loop []
           (log/info :fetch-contact!/looping {:ch ch})
           (when-let [msg (async/<! ch)]
             (log/info :fetch-contacts!/received {:msg msg})
             (let [tags (:tags msg)]
               (doseq [tag tags]
                 (log/info :fetch-contacts!/tag {:tag tag})
                 (let [[_p hex _relay] tag
                       target-id       (a.n.pubkeys/register-pubkey! hex)]
                   (register-contact! pubkey-id target-id))))
             (recur))))
       (throw (RuntimeException. "Failed to find pubkey"))))))

(>defn do-fetch-contacts!
  [ident]
  [::m.n.pubkeys/ident => any?]
  (log/info :do-fetch-contacts!/starting {:ident ident})
  (if-let [pubkey-id (::m.n.pubkeys/id ident)]
    (let [response (fetch-contacts! pubkey-id)]
      (log/info :do-fetch-contacts!/finished {:response response})
      {:status "ok"})
    (throw (RuntimeException. "Failed to find pubkey"))))

(comment

  (q.n.pubkey-contacts/read-record (first (q.n.pubkey-contacts/index-ids)))

  nil)
