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

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::password string?)
(s/def ::hashed-value string?)
(defattr password ::hashed-value :string
  {ao/identities      #{:account/id}
   ao/required?       true
   ao/schema          :production
   ::auth/permissions (fn [_] #{})})

(s/def ::salt string?)
(defattr password-salt ::salt :string
  {ao/identities      #{::id}
   ao/required?       true
   ao/schema          :production
   ::auth/permissions (fn [_] #{})})

(s/def ::iterations int?)
(defattr password-iterations ::iterations :int
  {ao/identities      #{::id}
   ao/required?       true
   ao/schema          :production
   ::auth/permissions (fn [_] #{})})

(def account-roles
  {:account.role/admin "Admin"
   :account.role/user  "User"})

(def role-keys (set (keys account-roles)))

(s/def ::role
  (s/with-gen
    (s/or :admin (constantly :account.role/admin)
          :user  (constantly :account.role/user))
    #(s/gen role-keys)))
(defattr role ::role :enum
  {ao/enumerated-labels account-roles
   ao/enumerated-values (set (keys account-roles))
   ao/identities        #{::id}
   ao/schema            :production})

(s/def ::input-params-valid (s/keys :req [::password ::id]))
(s/def ::input-params (s/keys :opt [::password ::name]))
(s/def ::params (s/keys :req [::hashed-value ::name ::salt ::iterations ::role]))
(s/def ::item (s/keys :req [::hashed-value ::id ::name  ::salt ::iterations ::role]))
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
   password
   password-salt
   password-iterations
   role])

#?(:clj (def resolvers []))
