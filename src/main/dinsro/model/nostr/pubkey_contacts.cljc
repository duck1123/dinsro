(ns dinsro.model.nostr.pubkey-contacts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]))

;; [[../../actions/nostr/pubkeys.clj][Pubkey Actions]]
;; [[../../joins/nostr/pubkeys.cljc][Pubkey Joins]]
;; [[../../model/nostr/pubkeys.cljc][Pubkey Models]]
;; [[../../ui/nostr/pubkey_contacts.cljs][Pubkey Contacts UI]]

;; https://github.com/nostr-protocol/nips/blob/master/02.md

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::actor uuid?)
(defattr actor ::actor :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.pubkeys/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.n.pubkeys/id ::m.n.pubkeys/hex]}})

(>def ::target uuid?)
(defattr target ::target :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.pubkeys/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.n.pubkeys/id ::m.n.pubkeys/hex]}})

(>def ::required-params (s/keys :req [::actor ::target]))
(>def ::params (s/keys :req [::actor ::target]))
(>def ::item (s/keys :req [::id ::actor ::target]))
(>def ::items (s/coll-of ::item))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn ident-item [item] [::item => ::ident] (select-keys item [::id]))
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id actor target])

#?(:clj (def resolvers []))
