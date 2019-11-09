(ns dinsro.model.rates
  (:require [java-time :as jt]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(s/def ::value double?)
(s/def ::params (s/keys :req [::value]))
(s/def ::prepared-params (s/keys :req [::value]))
(s/def ::item (s/keys :req [:db/id ::value]))

(def schema
  [{:db/ident ::value
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}])

(def rate {:id 1 :value 12158 :time (jt/instant)})
(def default-rates
  (map (fn [i]
         (-> rate
             (update :value (partial + i))
             (update :id (partial + i))))
       (range 7)))

(defn-spec prepare-record ::params
  [params ::params]
  params)

(defn-spec create-record ::item
  [params ::params]
  (let [tempid (d/tempid "rate-id")
        prepared-params (assoc (prepare-record params) :db/id tempid)
        response (d/transact db/*conn* {:tx-data [prepared-params]})]
    (get-in response [:tempids tempid])))

(defn-spec read-record ::item
  [id :db/id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::value)
      record)))

(defn-spec index-ids (s/coll-of :db/id)
  []
  (map first (d/q '[:find ?e :where [?e ::value _]] @db/*conn*)))

(defn-spec index-records (s/coll-of ::item :kind vector?)
  []
  (->> (index-ids)
       (d/pull-many @db/*conn* '[*])))

(defn-spec delete-record nil?
  [id :db/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]}))

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))
