(ns dinsro.model.currencies
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(s/def ::id pos-int?)
(s/def ::name string?)
(s/def ::params (s/keys :req [::name]))
(s/def ::currency (s/keys :req [::name]))

(def schema
  [{:db/ident       ::id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}


   ]
  )

(defn-spec index-ids (s/* ::id)
  []
  (map first (d/q '[:find ?e :where [?e ::name _]] @db/*conn*)))

(defn-spec index (s/* ::currency)
  []
  (->> (index-ids)
       (d/pull-many @db/*conn* '[::name :db/id])))

(defn-spec create-record ::currency
  [params ::params]
  (d/transact db/*conn* {:tx-data [params]})
  #_(db/create-currency! (assoc params :id nil))
  #_nil)
