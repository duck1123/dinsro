(ns dinsro.model.ln.accounts
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

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities       #{::id}
   ao/target           ::m.ln.nodes/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.ln.nodes/id ::m.ln.nodes/name]}})

(s/def ::raw-params
  (s/keys :req []))
(s/def ::params
  (s/keys :req [::node]))
(s/def ::item
  (s/keys :req [::id ::node]))

(defn idents
  [ids]
  (map (fn [id] {::id id}) ids))

(def attributes
  [id node])
