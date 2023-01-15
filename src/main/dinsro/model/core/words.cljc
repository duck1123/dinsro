(ns dinsro.model.core.words
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::mnemonic uuid?)
(defattr mnemonic ::mnemonic :ref
  {ao/identities       #{::id}
   ao/target           ::m.c.mnemonics/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.c.mnemonics/id]}})

(>def ::word string?)
(defattr word ::word :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::position number?)
(defattr position ::position :int
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::required-params (s/keys :req [::word ::position ::mnemonic]))
(>def ::params  (s/keys :req [::word ::position ::mnemonic]))
(>def ::item (s/keys :req [::id ::word ::position ::mnemonic]))
(>def ::items (s/coll-of ::item))

(s/def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id word position mnemonic])
