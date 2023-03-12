(ns dinsro.model.nostr.relays
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.specs]))

;; [[../../actions/nostr/relays.clj][Relay Actions]]
;; [[../../joins/nostr/relays.cljc][Relay Joins]]
;; [[../../queries/nostr/relays.clj][Relay Queries]]
;; [[../../mutations/nostr/relays.cljc][Relay Mutations]]

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::address string?)
(defattr address ::address :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::connected boolean?)
(defattr connected ::connected :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::required-params (s/keys :req [::address]))
(s/def ::params (s/keys :req [::address] :opt [::connected]))
(s/def ::item (s/keys :req [::id ::address ::connected]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id address connected])
