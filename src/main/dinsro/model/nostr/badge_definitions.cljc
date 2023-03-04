(ns dinsro.model.nostr.badge-definitions
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

(>def ::code string?)
(defattr code ::code :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::description string?)
(defattr description ::description :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::image-url string?)
(defattr image-url ::image-url :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::thumbnail-url string?)
(defattr thumbnail-url ::thumbnail-url :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::pubkey uuid?)
(defattr pubkey ::pubkey :ref
  {ao/identities       #{::id}
   ao/schema           :production
   ::report/column-EQL {::node [::m.n.pubkeys/id ::m.n.pubkeys/hex]}})

(>def ::required-params (s/keys :req [::code ::description ::image-url ::thumbnail-url ::pubkey]))

(>def ::params (s/keys :req [::code ::description ::image-url ::thumbnail-url ::pubkey]))
(>def ::item (s/keys :req [::id ::code ::description ::image-url ::thumbnail-url ::pubkey]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn ident-item [item] [::item => ::ident] (select-keys item [::id]))
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id code description image-url thumbnail-url pubkey])

#?(:clj (def resolvers []))
