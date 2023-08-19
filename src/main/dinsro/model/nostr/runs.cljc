(ns dinsro.model.nostr.runs
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn => ?]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.specs :as ds]))

;; [[../../actions/nostr/runs.clj]]

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

;; initial started stopped
(>def ::status #{:initial :started :stopped :finished :errored})
(defattr status ::status :keyword
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::request uuid?)
(defattr request ::request :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.requests/id
   ao/schema           :production
   ::report/column-EQL {::request [::m.n.requests/id ::m.n.requests/code]}})

(>def ::connection uuid?)
(defattr connection ::connection :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.connections/id
   ao/schema           :production
   ::report/column-EQL {::connection [::m.n.connections/id ::m.n.connections/status]}})

(s/def ::start-time (? ::ds/date))
(defattr start-time ::start-time :instant
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::finish-time (? ::ds/date))
(defattr finish-time ::finish-time :instant
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::end-time (? ::ds/date))
(defattr end-time ::end-time :instant
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::params (s/keys :req [::request ::connection]))
(>def ::item (s/keys :req [::id ::request ::connection ::start-time ::end-time
                           ::finish-time
                           ::status]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id status request start-time end-time finish-time connection])
