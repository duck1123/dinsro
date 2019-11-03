(ns dinsro.model.account
  (:require [datahike.api :as d]
            [dinsro.db.core :as db]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(def schema
  [{:db/ident       :account/id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident       :account/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])

(defn prepare-account
  [params]
  params)

(defn create-account!
  [params]
  (let [response (d/transact db/*conn* {:tx-data [params]})]
    (get-in response [:tempids :db/current-tx])))

(defn delete-account!
  [id]
  (db/delete-account! {:id id}))

(defn index-account-ids
  []
  (map first (d/q '[:find ?e :where [?e :account/name _]] @db/*conn*)))

(defn-spec index-records any?
  []
  (d/pull-many @db/*conn* '[*] (index-account-ids)))
