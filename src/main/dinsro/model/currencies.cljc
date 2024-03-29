(ns dinsro.model.currencies
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::code string?)
(defattr code ::code :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::params (s/keys :req [::code ::name]))
(>def ::item-opt (s/keys :opt [::code ::name]))
(>def ::item (s/keys :req [::id ::code ::name]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [code id name])
