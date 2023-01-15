(ns dinsro.model.core.wallets
  (:refer-clojure :exclude [key name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.users :as m.users]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::derivation string?)
(defattr derivation ::derivation :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::key string?)
(defattr key ::key :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::ext-public-key string?)
(defattr ext-public-key ::ext-public-key :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::ext-private-key (s/or :key string? :nil nil?))
(defattr ext-private-key ::ext-private-key :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::mnemonic (s/or :id uuid? :nil nil?))
(defattr mnemonic ::mnemonic :ref
  {ao/identities       #{::id}
   ao/target           ::m.c.mnemonics/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.c.mnemonics/id]}})

(>def ::network uuid?)
(defattr network ::network :ref
  {ao/identities #{::id}
   ao/target     ::m.c.networks/id
   ao/schema     :production
   ::report/column-EQL {::node [::m.c.networks/id ::m.c.networks/name]}})

(s/def ::user uuid?)
(defattr user ::user :ref
  {ao/identities #{::id}
   ao/target     ::m.users/id
   ao/schema     :production
   ::report/column-EQL {::user [::m.users/id ::m.users/name]}})

(>def ::required-params
  (s/keys :req [::name ::network]
          :opt [::mnemonic]))
(>def ::params
  (s/keys :req [::name ::network]
          :opt [::mnemonic]))
(>def ::item
  (s/keys :req [::id ::name ::network]
          :opt [::mnemonic]))
(>def ::items (s/coll-of ::item))

(s/def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id name derivation key network mnemonic user ext-public-key ext-private-key])
