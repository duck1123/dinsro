(ns dinsro.model.core.chains
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req [::name]))
(s/def ::item (s/keys :req [::id ::name]))
(s/def ::items (s/coll-of ::item))

(defn ident [id] {::id id})
(defn idents [ids] (mapv ident ids))

(def attributes [id name])
