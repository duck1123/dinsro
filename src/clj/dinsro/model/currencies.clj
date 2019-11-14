(ns dinsro.model.currencies
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre])
  (:import datahike.db.TxReport))

(s/def ::id pos-int?)
(s/def ::name string?)
(s/def ::params (s/keys :req [::name]))
(s/def ::item (s/keys :req [::name]))

(def schema
  [{:db/ident       ::id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])

(defn-spec index-ids (s/* ::id)
  []
  (map first (d/q '[:find ?e :where [?e ::name _]] @db/*conn*)))

(defn-spec index (s/* ::item)
  []
  (->> (index-ids)
       (d/pull-many @db/*conn* '[::name :db/id])))

(defn-spec create-record ::id
  [params ::params]
  (let [params (assoc params :db/id "currency-id")]
    (let [response (d/transact db/*conn* {:tx-data [params]})]
      (get-in response [:tempids "currency-id"]))))

(defn-spec read-record (s/nilable ::item)
  [id :db/id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::name)
      record)))

(defn-spec delete-record TxReport
  [id :db/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]}))

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(defn-spec mock-record ::item
  []
  (let [params (gen/generate (s/gen ::params))
        id (create-record params)]
    (read-record id)))
