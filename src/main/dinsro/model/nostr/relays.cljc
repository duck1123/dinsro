(ns dinsro.model.nostr.relays
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.specs]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::address string?)
(defattr address ::address :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::required-params
  (s/keys :req [::address]))

(def required-params
  "Required params for contacts"
  ::required-params)

(s/def ::params (s/keys :req [::address]))
(s/def ::item (s/keys :req [::id ::address]))

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes [id address])

#?(:clj (def resolvers []))
