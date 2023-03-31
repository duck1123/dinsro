(ns dinsro.model.nostr.event-tags
  (:refer-clojure :exclude [type])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]))

;; [[../../joins/nostr/event_tags.cljc][Event Tag Joins]]

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::index number?)
(defattr index ::index :number
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::parent uuid?)
(defattr parent ::parent :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.events/id
   ao/schema           :production
   ::report/column-EQL {::parent [::m.n.events/id ::m.n.events/note-id]}})

(>def ::type string?)
(defattr type ::type :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::raw-value string?)
(defattr raw-value ::raw-value :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::extra (? string?))
(defattr extra ::extra :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::event (? uuid?))
(defattr event ::event :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.events/id
   ao/schema           :production
   ::report/column-EQL {::event [::m.n.events/id ::m.n.events/note-id]}})

(>def ::pubkey (? uuid?))
(defattr pubkey ::pubkey :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.pubkeys/id
   ao/schema           :production
   ::report/column-EQL {::pubkey [::m.n.pubkeys/id ::m.n.pubkeys/hex]}})

(>def ::required-params (s/keys :req [::index ::parent]
                                :opt [::event ::pubkey]))
(>def ::params (s/keys :req [::index ::parent ::raw-value ::type ::extra]
                       :opt [::event ::pubkey]))
(>def ::item (s/keys :req [::id ::index ::parent ::raw-value ::type ::extra]
                     :opt [::event ::pubkey]))
(>def ::items (s/coll-of ::item))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id index parent event pubkey type raw-value extra])
