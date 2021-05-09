(ns dinsro.model.users
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.components.database-queries :as queries])
   [taoensso.timbre :as log]))

(s/def ::password string?)

(s/def ::id string?)

(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(defattr id ::id :string
  {ao/identity? true
   ao/schema    :production})

(s/def ::password-hash string?)

(defattr password-hash ::password-hash :string
  {ao/identities #{::id}
   ao/schema     :production})

(def password-hash-spec
  {:db/ident       ::password-hash
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::input-params-valid (s/keys :req [::password ::id]))

(s/def ::input-params (s/keys :opt [::password ::id]))

(s/def ::params (s/keys :req [::password-hash ::id]))

(s/def ::item (s/keys :req [::password-hash ::id]))

(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(defattr all-users ::all-users :ref
  {ao/target    ::id
   ao/pc-output [{::all-users [::id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     #?(:clj
        {::all-users (queries/get-all-users env query-params)}
        :cljs
        (comment env query-params)))})

(def schema
  [password-hash-spec
   id-spec])

(def attributes [id password-hash all-users])

#?(:clj
   (def resolvers []))
