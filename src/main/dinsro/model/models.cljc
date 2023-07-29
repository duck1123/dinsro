(ns dinsro.model.models
  "The model for describing all models"
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [=> >def >defn]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

(defonce models-atom (atom {}))

(defattr id ::id :keyword
  {ao/identity? true})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req [::rate ::date ::source]))
(s/def ::item (s/keys :req [::id ::rate ::date ::source]))
(>def ::ident (s/keys :req [::id]))

(>defn ident [id] [::id => any?] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => any?] (mapv ident ids))

(def attributes
  [id name])

(defn defmodel
  [key options]
  (swap! models-atom assoc key options))
