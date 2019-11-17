(ns dinsro.spec.users
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(s/def ::name string?)
(s/def ::email (s/with-gen #(and % (re-matches #".+@.+\..+" %)) (fn [] ds/email-gen)))
(s/def ::password string?)
(s/def ::password-hash string?)
(s/def ::params (s/keys :req [::name ::email ::password]))
(s/def ::item (s/keys :req [::name ::email ::password-hash]))

(def schema
  [{:db/ident       ::id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::password-hash
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::email
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])
