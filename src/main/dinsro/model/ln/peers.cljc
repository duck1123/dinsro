(ns dinsro.model.ln.peers
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]))

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

(s/def ::remote-node uuid?)
(defattr remote-node ::remote-node :ref
  {ao/target           ::m.ln.remote-nodes/id
   ao/identities       #{::id}
   ao/schema           :production
   ::report/column-EQL {::remote-node [::m.ln.remote-nodes/id ::m.ln.remote-nodes/pubkey]}})

(s/def ::inbound? boolean?)
(defattr inbound? ::inbound? :boolean
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

(s/def ::params (s/keys :req [::inbound? ::sat-sent ::sat-recv
                              ::node
                              ::remote-node]))
(s/def ::item (s/keys :req [::id
                            ::inbound? ::sat-sent ::sat-recv ::node ::remote-node]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id inbound? sat-sent node remote-node])
