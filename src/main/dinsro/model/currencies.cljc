(ns dinsro.model.currencies
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.components.database-queries :as queries])))

(s/def ::id string?)
(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(defattr id ::id :string
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)

(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req [::id ::name]))

(s/def ::item-opt (s/keys :opt [::id ::name]))

(s/def ::item (s/keys :req [::id ::name]))

(s/def ::ident (s/tuple keyword? ::id))

(defattr all-currencies ::all-currencies :ref
  {ao/target    ::id
   ao/pc-output [{::all-currencies [::id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     #?(:clj
        {::all-currencies (queries/get-all-currencies env query-params)}
        :cljs
        (comment env query-params)))})

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(def schema
  [id-spec
   name-spec])

(def attributes [id name])

#?(:clj
   (def resolvers []))
