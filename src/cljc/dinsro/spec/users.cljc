(ns dinsro.spec.users
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(s/def ::password string?)

(s/def ::name string?)
(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::password-hash string?)
(def password-hash-spec
  {:db/ident       ::password-hash
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::email (s/with-gen #(and % (re-matches #".+@.+\..+" %)) (fn [] ds/email-gen)))
(def email-spec
  {:db/ident       ::email
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::params (s/keys :req [::name ::email ::password]))
(s/def ::item (s/keys :req [::name ::email ::password-hash]))

(def schema
  [name-spec password-hash-spec email-spec])

(comment
  (gen/generate (s/gen ::params))
  (gen/generate (s/gen ::item))

  )
