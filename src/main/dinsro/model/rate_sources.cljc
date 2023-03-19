(ns dinsro.model.rate-sources
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [=> >def >defn]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.specs]))

(s/def ::id        uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::url string?)
(defattr url ::url :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::active? boolean?)
(defattr active? ::active? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::identity? boolean?)
(defattr identity? ::identity? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::path string?)
(defattr path ::path :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::currency ::m.currencies/id)
(defattr currency ::currency :ref
  {ao/target     ::m.currencies/id
   ao/identities #{::id}
   ao/schema     :production
   ::report/column-EQL {::currency [::m.currencies/id ::m.currencies/name]}})

(s/def ::required-params (s/keys :req [::name ::url ::active? ::path ::identity?]))
(s/def ::params (s/keys :req [::name ::url ::currency ::active? ::path ::identity?]))
(s/def ::item (s/keys :req [::id ::name ::url ::currency ::active? ::path ::identity?]))
(>def ::ident (s/keys :req [::id]))

(>defn ident [id] [::id => any?] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [currency id name url active? path identity?])
