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

(s/def ::id    number?)
(s/def ::name    string?)
(s/def ::params  (s/keys :req [::name]))
(s/def ::account (s/keys :req [::name]))

(defn prepare-account
  [params]
  params)

(defn-spec create-account! any?
  [params ::params]
  (let [response (d/transact db/*conn* {:tx-data [params]})]
    (get-in response [:tempids :db/current-tx])))

(defn delete-account!
  [id]
  #_(db/delete-account! {:id id})
  nil)

(defn read-account
  [id]
  nil)

(defn index-account-ids
  []
  (map first (d/q '[:find ?e :where [?e :account/name _]] @db/*conn*)))

(defn-spec index-records any?
  []
  (d/pull-many @db/*conn* '[*] (index-account-ids)))
