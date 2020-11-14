(ns dinsro.queries.transactions
  (:require
   [clojure.spec.alpha :as s]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(defn create-record
  [params]
  (let [tempid (d/tempid "transaction-id")
        prepared-params (-> params
                            (assoc  :db/id tempid)
                            (update ::m.transactions/date tick/inst))
        response (d/transact db/*conn* {:tx-data [prepared-params]})]
    (get-in response [:tempids tempid])))

(s/fdef create-record
  :args (s/cat :params ::m.transactions/params)
  :ret ::ds/id)

(defn read-record
  [id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.transactions/value)
      (update record ::m.transactions/date tick/instant))))

(s/fdef read-record
  :args (s/cat :id ::ds/id)
  :ret  (s/nilable ::m.transactions/item))

(defn index-ids
  []
  (map first (d/q '[:find ?e :where [?e ::m.transactions/value _]] @db/*conn*)))

(s/fdef index-ids
  :args (s/cat)
  :ret (s/coll-of ::ds/id))

(defn index-records
  []
  (->> (index-ids)
       (d/pull-many @db/*conn* '[*])
       (map #(update % ::m.transactions/date tick/instant))))

(s/fdef index-records
  :args (s/cat)
  :ret (s/coll-of ::m.transactions/item))

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
