(ns dinsro.model.nostr.events
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.specs]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::note-id string?)
(defattr note-id ::note-id :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::pubkey uuid?)
(defattr pubkey ::pubkey :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.pubkeys/id
   ao/schema           :production
   ::report/column-EQL {::pubkey [::m.n.pubkeys/id
                                  ::m.n.pubkeys/hex
                                  ::m.n.pubkeys/name]}})

(>def ::created-at number?)
(defattr created-at ::created-at :number
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::kind number?)
(defattr kind ::kind :number
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::content string?)
(defattr content ::content :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::sig string?)
(defattr sig ::sig :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::deleted boolean?)
(defattr deleted? ::deleted? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::required-params (s/keys :req []))
(>def ::params (s/keys :req []))
(>def ::item (s/keys :req [::id]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id note-id pubkey created-at kind content sig deleted?])
