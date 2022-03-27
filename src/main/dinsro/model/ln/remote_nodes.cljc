(ns dinsro.model.ln.remote-nodes
  (:refer-clojure :exclude [alias])
  (:require
   [clojure.spec.alpha :as s]
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

(s/def ::color string?)
(defattr color ::color :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::alias string?)
(defattr alias ::alias :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::num-channels number?)
(defattr num-channels ::num-channels :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities       #{::id}
   ao/target           ::m.ln.nodes/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.ln.nodes/id ::m.ln.nodes/name]}})

(s/def ::params
  (s/keys :req [::pubkey ::node]
          :opt [::color ::alias ::num-channels]))
(s/def ::item
  (s/keys :req [::id ::pubkey ::node]
          :opt [::color ::alias ::num-channels]))

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes
  [id pubkey color alias num-channels node])
