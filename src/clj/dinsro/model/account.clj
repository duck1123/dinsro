(ns dinsro.model.account
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre])
  (:import datahike.db.TxReport))

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

(defn-spec read-account (s/nilable ::account)
  [id ::id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::name)
      record)))

(defn-spec index-ids (s/* ::id)
  []
  (map first (d/q '[:find ?e :where [?e ::name _]] @db/*conn*)))

(defn-spec index-records (s/* ::account)
  []
  (d/pull-many @db/*conn* '[*] (index-ids)))

(defn-spec delete-record TxReport
  [id :db/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]}))

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(defn-spec mock-account ::account
  []
  (let [params (gen/generate (s/gen ::params))
        id (create-account! params)]
    (read-account id)))
