(ns dinsro.model.core.addresses
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::address string?)
(defattr address ::address :string
  {ao/identities #{::id}
   ao/schema     :production})

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(s/def ::params (s/keys :req [::address]))
(s/def ::item (s/keys :req [::id ::address]))

(def attributes [id address])
