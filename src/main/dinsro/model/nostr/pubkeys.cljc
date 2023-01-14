(ns dinsro.model.nostr.pubkeys
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
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

(s/def ::required-params (s/keys :req [::pubkey]))
(s/def ::params (s/keys :req [::pubkey]))
(s/def ::item (s/keys :req [::id ::pubkey]))
(s/def ::items (s/coll-of ::item))
(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => any?]
  {::id id})

(>defn ident-item
  [{::keys [id]}]
  [::item => any?]
  (ident id))

(>defn idents
  [ids]
  [(s/coll-of ::id) => any?]
  (mapv ident ids))

(def attributes [id pubkey])

#?(:clj (def resolvers []))
