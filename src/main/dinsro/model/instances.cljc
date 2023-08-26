(ns dinsro.model.instances
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [=> ? >def >defn]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.specs :as ds]))

;; [[../actions/instances.clj]]
;; [[../queries/instances.clj]]
;; [[../../../notebooks/dinsro/notebooks/instances_notebook.clj]]

(s/def ::id        uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::created-time (? ::ds/date))
(defattr created-time ::created-time :instant
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::last-heartbeat (? ::ds/date))
(defattr last-heartbeat ::last-heartbeat :instant
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req []))
(s/def ::item (s/keys :req [::id ::created-time ::last-heartbeat]))
(>def ::ident (s/keys :req [::id]))

(>defn ident [id] [::id => any?] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id created-time last-heartbeat])
