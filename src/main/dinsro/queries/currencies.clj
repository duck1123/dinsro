(ns dinsro.queries.currencies
  (:require
   [clojure.spec.alpha :as s]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]))

(defn index-ids
  []
  (map first (d/q '[:find ?e :where [?e ::m.currencies/name _]] @db/*conn*)))

(s/fdef index-ids
  :args (s/cat)
  :ret (s/coll-of ::ds/id))

(defn index-records
  []
  (d/pull-many @db/*conn* '[::m.currencies/name :db/id] (index-ids)))

(s/fdef index-records
  :args (s/cat)
  :ret (s/coll-of ::m.currencies/item))

(defn create-record
  [params]
  (let [params (assoc params :db/id "currency-id")
        response (d/transact db/*conn* {:tx-data [params]})]
    (get-in response [:tempids "currency-id"])))

(s/fdef create-record
  :args (s/cat :params ::m.currencies/params)
  :ret ::ds/id)

(defn read-record
  [id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.currencies/name)
      record)))

(s/fdef read-record
  :args (s/cat :id ::ds/id)
  :ret  (s/nilable ::m.currencies/item))

(defn delete-record
  [id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(s/fdef delete-record
  :args (s/cat :id ::ds/id)
  :ret nil?)

(defn delete-all
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(s/fdef delete-all
  :args (s/cat)
  :ret nil?)
