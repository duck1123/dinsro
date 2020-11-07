(ns dinsro.model.transactions
  (:require
   [clojure.spec.alpha :as s]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.specs :as ds]
   [dinsro.specs.transactions :as s.transactions]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(defn create-record
  [params]
  (let [tempid (d/tempid "transaction-id")
        prepared-params (-> params
                            (assoc  :db/id tempid)
                            (update ::s.transactions/date tick/inst))
        response (d/transact db/*conn* {:tx-data [prepared-params]})]
    (get-in response [:tempids tempid])))

(s/fdef create-record
  :args (s/cat :params ::s.transactions/params)
  :ret ::ds/id)

(defn read-record
  [id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::s.transactions/value)
      (update record ::s.transactions/date tick/instant))))

(s/fdef read-record
  :args (s/cat :id ::ds/id)
  :ret  (s/nilable ::s.transactions/item))

(defn index-ids
  []
  (map first (d/q '[:find ?e :where [?e ::s.transactions/value _]] @db/*conn*)))

(s/fdef index-ids
  :args (s/cat)
  :ret (s/coll-of ::ds/id))

(defn index-records
  []
  (->> (index-ids)
       (d/pull-many @db/*conn* '[*])
       (map #(update % ::s.transactions/date tick/instant))))

(s/fdef index-records
  :args (s/cat)
  :ret (s/coll-of ::s.transactions/item))

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
