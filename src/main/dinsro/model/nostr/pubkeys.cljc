(ns dinsro.model.nostr.pubkeys
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

;; [[../../actions/nostr/pubkeys.clj][Pubkeys Actions]]
;; [[../../joins/nostr/pubkeys.cljc][Pubkey Joins]]
;; [[../../queries/nostr/pubkeys.clj][Pubkey Queries]]
;; [[../../ui/nostr/pubkeys.cljs][Pubkeys UI]]

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::hex string?)
(defattr hex ::hex :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::name (? string?))
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::picture (? string?))
(defattr picture ::picture :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::about (? string?))
(defattr about ::about :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::nip05 (? string?))
(defattr nip05 ::nip05 :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::website (? string?))
(defattr website ::website :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::lud16 (? string?))
(defattr lud16 ::lud16 :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::lud06 (? string?))
(defattr lud06 ::lud06 :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::banner (? string?))
(defattr banner ::banner :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::required-params (s/keys :req [::hex]))
(>def ::params (s/keys :req [::hex]
                       :opt [::name ::picture ::about ::nip05 ::website ::lud16 ::lud06 ::banner]))
(>def ::item (s/keys :req [::id ::hex]
                     :opt [::name ::picture ::about ::nip05 ::website ::lud16 ::lud06 ::banner]))
(>def ::items (s/coll-of ::item))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id hex name picture about nip05 website lud16 lud06 banner])
