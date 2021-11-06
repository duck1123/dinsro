(ns dinsro.model.rate-sources
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.components.database-queries :as queries])
   [dinsro.model.currencies :as m.currencies]
   [dinsro.specs]))

(s/def ::id        uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::url string?)
(defattr url ::url :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::currency ::m.currencies/id)
(defattr currency ::currency :ref
  {ao/target     ::m.currencies/id
   ao/identities #{::id}
   ao/schema     :production})

(s/def ::required-params (s/keys :req [::name ::url]))
(s/def ::params (s/keys :req [::name ::url ::currency]))
(s/def ::item (s/keys :req [::id ::name ::url ::currency]))
(s/def ::items (s/coll-of ::item))
(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(defattr all-rate-sources ::all-rate-sources :ref
  {ao/target    ::id
   ao/pc-output [{::all-rate-sources [::id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     #?(:clj
        {::all-rate-sources (queries/get-all-rate-sources env query-params)}
        :cljs
        (comment env query-params)))})

(def attributes [all-rate-sources currency id name url])

#?(:clj (def resolvers []))
