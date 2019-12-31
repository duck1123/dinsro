(ns dinsro.spec.categories
  (:require [clojure.spec.alpha :as s]
            [dinsro.specs :as ds]))

(s/def ::name             string?)
(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::user-id          ::ds/id)
(s/def ::user             (s/keys :req [
                                        ;; ::ds/id
                                        ]))
(def user-spec
  {:db/ident       ::user
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::params           (s/keys :req [::name ::user]))
(s/def ::item             (s/keys :req [::name ::user]))
(def item-spec
  {:db/ident ::item
   :db.entity/attrs [::name ::user]})

(def schema
  [name-spec
   user-spec
   #_item-spec])
