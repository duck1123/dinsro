(ns dinsro.model.account
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(def schema
  [{:db/ident       ::id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])

(s/def ::id      pos-int?)
(s/def ::name    string?)
(s/def ::params  (s/keys :req [::name]))
(s/def ::account (s/keys :req [::name]))

(defn-spec prepare-account ::params
  [params ::params]
  params)

(defn-spec create-account! ::id
  [params ::params]
  (let [response (d/transact db/*conn* {:tx-data [(assoc params :db/id "account-id")]})]
    (get-in response [:tempids "account-id"])))

(defn-spec delete-account! nil?
  [id ::id]
  #_(db/delete-account! {:id id})
  nil)

(defn-spec read-account (s/nilable ::account)
  [id ::id]
  (d/pull @db/*conn* '[::name] id))

(defn-spec index-account-ids (s/* ::id)
  []
  (map first (d/q '[:find ?e :where [?e ::name _]] @db/*conn*)))

(defn-spec index-records (s/* ::account)
  []
  (d/pull-many @db/*conn* '[*] (index-account-ids)))
