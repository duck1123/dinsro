(ns dinsro.model.core.networks
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.chains :as m.c.chains]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::chain uuid?)
(defattr chain ::chain :ref
  {ao/identities       #{::id}
   ao/target           ::m.c.chains/id
   ao/schema           :production
   ::report/column-EQL {::chain [::m.c.chains/id ::m.c.chains/name]}})

(>def ::params (s/keys :req [::chain ::name]))
(>def ::item (s/keys :req [::id ::chain ::name]))
(>def ::items (s/coll-of ::item))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id chain name])
