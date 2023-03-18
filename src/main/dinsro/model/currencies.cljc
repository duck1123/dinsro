(ns dinsro.model.currencies
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::code string?)
(defattr code ::code :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req [::code ::name]))
(s/def ::item (s/keys :req [::id ::code ::name]))
(s/def ::ident (s/tuple keyword? ::id))

(>defn ident [id] [::id => any?] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => any?] (mapv ident ids))

(def attributes [code id name])
