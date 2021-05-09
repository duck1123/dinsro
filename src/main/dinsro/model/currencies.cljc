(ns dinsro.model.currencies
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.components.database-queries :as queries])
   [taoensso.timbre :as log]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::code string?)
(defattr code ::code :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req [::code ::name]))
(s/def ::item-opt (s/keys :opt [::code ::name]))
(s/def ::item (s/keys :req [::id ::code ::name]))
(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(defattr all-currencies ::all-currencies :ref
  {ao/target    ::id
   ao/pc-output [{::all-currencies [::id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     #?(:clj
        {::all-currencies (queries/get-all-currencies env query-params)}
        :cljs
        (comment env query-params)))})

(defattr link ::link :ref
  {ao/cardinality :one
   ao/target      ::id
   ao/pc-input    #{::id}
   ao/pc-output   [{::link [::id]}]
   ao/pc-resolve  (fn [_env params] {::link params})})

(defattr currency-accounts ::currency-accounts :ref
  {ao/target     ::id
   ao/pc-input   #{::id}
   ao/pc-output  [{::currency-accounts [::id]}]
   ao/pc-resolve (fn [_env params] {::currency-accounts params})})

(def attributes [all-currencies code currency-accounts id link name])

#?(:clj (def resolvers []))

(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(def schema
  [id-spec
   name-spec])
