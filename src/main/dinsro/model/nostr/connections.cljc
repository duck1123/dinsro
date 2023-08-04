(ns dinsro.model.nostr.connections
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn => ?]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.specs :as ds]))

;; [[../../actions/nostr/connections.clj]]
;; [[../../queries/nostr/connections.clj]]

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::end-time (? ::ds/date))
(defattr end-time ::end-time :instant
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::relay uuid?)
(defattr relay ::relay :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.relays/id
   ao/schema           :production
   ::report/column-EQL {::relay [::m.n.relays/id ::m.n.relays/address]}})

(s/def ::start-time (? ::ds/date))
(defattr start-time ::start-time :instant
  {ao/identities #{::id}
   ao/schema     :production})

;; initial started stopped
(>def ::status #{:initial :connecting :connected :disconnected :errored})
(defattr status ::status :keyword
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::params (s/keys :req [::relay]))
(>def ::item (s/keys :req [::id ::relay ::status]
                     :opt [::start-time ::end-time]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id end-time relay start-time status])
