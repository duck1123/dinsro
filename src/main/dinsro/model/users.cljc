(ns dinsro.model.users
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.authorization :as auth]
   [taoensso.timbre :as log]))

(def default-username "admin")
(def default-password "hunter2")

(s/def ::password string?)

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::password-hash string?)
(defattr password-hash ::password-hash :string
  {ao/required?       true
   ao/identities      #{::id}
   ::auth/permissions (fn [_] #{})
   ao/schema          :production})

(s/def ::input-params-valid (s/keys :req [::password ::id]))
(s/def ::input-params (s/keys :opt [::password ::name]))
(s/def ::params (s/keys :req [::password-hash ::name]))
(s/def ::item (s/keys :req [::password-hash ::id ::name]))
(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(def attributes
  [id
   name
   password-hash])

#?(:clj (def resolvers []))
