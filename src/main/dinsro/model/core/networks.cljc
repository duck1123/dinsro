(ns dinsro.model.core.networks
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.chains :as m.c.chains]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::chain uuid?)
(defattr chain ::chain :ref
  {ao/identities       #{::id}
   ao/target           ::m.c.chains/id
   ao/schema           :production
   ::report/column-EQL {::chain [::m.c.chains/id ::m.c.chains/name]}})

(s/def ::params (s/keys :req [::chain ::name]))
(s/def ::item (s/keys :req [::id ::chain ::name]))
(s/def ::items (s/coll-of ::item))

(def link-query [::id ::name])

(defn ident
  [id]
  {::id id})

(defn ident-item
  [{::keys [id]}]
  (ident id))

(defn idents
  [ids]
  (mapv ident ids))

(def attributes [id chain name])
