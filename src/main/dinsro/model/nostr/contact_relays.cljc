(ns dinsro.model.nostr.contact-relays
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.specs]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::contact uuid?)
(defattr contact ::contact :ref
  {ao/identities       #{::id}
   ao/target           ::m.contacts/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.contacts/id ::m.contacts/name]}})

(s/def ::relay uuid?)
(defattr relay ::relay :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.relays/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.n.relays/id ::m.n.relays/address]}})

(>def ::required-params
  (s/keys :req []))

(s/def ::params (s/keys :req []))
(s/def ::item (s/keys :req [::id]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id contact relay])
