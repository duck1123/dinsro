(ns dinsro.model.nostr.events
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

(>def ::required-params
  (s/keys :req []))

(def required-params
  "Required params for contacts"
  ::required-params)

(s/def ::params (s/keys :req []))
(s/def ::item (s/keys :req [::id]))

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes [id])

#?(:clj (def resolvers []))
