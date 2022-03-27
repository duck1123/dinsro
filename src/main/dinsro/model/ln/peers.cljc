(ns dinsro.model.ln.peers
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.ln.nodes :as m.ln.nodes]))

(def rename-map
  {:address    ::address
   :flapCount  ::flap-count
   :inbound    ::inbound
   :pubKey     ::pubkey
   :satRecv    ::sat-recv
   :satSent    ::sat-sent
   :syncType   ::sync-type
   :pingTime   ::ping-time
   :bytesSent  ::bytes-sent
   :bytesRecv  ::bytes-recv
   :lastFlapNs ::last-flap-ns})

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/target           ::m.ln.nodes/id
   ao/identities       #{::id}
   ao/schema           :production
   ::report/column-EQL {::node [::m.ln.nodes/id ::m.ln.nodes/name]}})

(s/def ::address string?)
(defattr address ::address :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::pubkey (s/or
                 :nil nil?
                 :string string?))
(defattr pubkey ::pubkey :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::inbound boolean?)
(defattr inbound ::inbound :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::sat-sent number?)
(defattr sat-sent ::sat-sent :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::sat-recv number?)
(defattr sat-recv ::sat-recv :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req [::address ::pubkey ::inbound ::sat-sent ::sat-recv]))
(s/def ::item (s/keys :req [::id ::address ::pubkey ::inbound ::sat-sent ::sat-recv ::node]))

(>defn idents
  [ids]
  [(s/coll-of ::id) => (s/coll-of (s/keys))]
  (map (fn [id] {::id id}) ids))

(def attributes [id address pubkey inbound sat-sent node])
