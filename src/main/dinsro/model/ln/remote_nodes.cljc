(ns dinsro.model.ln.remote-nodes
  (:refer-clojure :exclude [alias])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.ln.nodes :as m.ln.nodes]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::pubkey string?)
(defattr pubkey ::pubkey :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::host string?)
(defattr host ::host :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::color string?)
(defattr color ::color :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::alias string?)
(defattr alias ::alias :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities       #{::id}
   ao/target           ::m.ln.nodes/id
   ao/schema           :production
   ::report/column-EQL {::network [::m.ln.nodes/id ::m.ln.nodes/name]}})

(s/def ::num-channels number?)
(defattr num-channels ::num-channels :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params
  (s/keys :req [::pubkey ::node]
          :opt [::color ::alias ::host]))
(s/def ::item
  (s/keys :req [::id ::pubkey ::node]
          :opt [::color ::alias ::host]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes
  [id pubkey color host alias num-channels])
