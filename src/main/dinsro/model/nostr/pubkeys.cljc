(ns dinsro.model.nostr.pubkeys
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::pubkey string?)
(defattr pubkey ::pubkey :string
  {ao/identities #{::id}
   ao/schema     :production})

;; name

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

;; picture

(s/def ::picture string?)
(defattr picture ::picture :string
  {ao/identities #{::id}
   ao/schema     :production})

;; about

(s/def ::about string?)
(defattr about ::about :string
  {ao/identities #{::id}
   ao/schema     :production})

;; nip05

(s/def ::nip05 string?)
(defattr nip05 ::nip05 :string
  {ao/identities #{::id}
   ao/schema     :production})

;; website

(s/def ::website string?)
(defattr website ::website :string
  {ao/identities #{::id}
   ao/schema     :production})

;; lud16

(s/def ::lud16 string?)
(defattr lud16 ::lud16 :string
  {ao/identities #{::id}
   ao/schema     :production})

;; lud06

(s/def ::lud06 string?)
(defattr lud06 ::lud06 :string
  {ao/identities #{::id}
   ao/schema     :production})

;; banner

(s/def ::banner string?)
(defattr banner ::banner :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req [::pubkey]
                        :opt [::name ::picture ::about ::nip05 ::website ::lud16 ::lud06 ::banner]))
(s/def ::item (s/keys :req [::id ::pubkey]
                      :opt [::name ::picture ::about ::nip05 ::website ::lud16 ::lud06 ::banner]))
(s/def ::items (s/coll-of ::item))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id pubkey name picture about nip05 website lud16 lud06 banner])
