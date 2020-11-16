(ns dinsro.queries.rates
  (:require
   [clojure.spec.alpha :as s]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.rates :as m.rates]
   [dinsro.specs :as ds]
   [dinsro.streams :as streams]
   [manifold.stream :as ms]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(def record-limit 75)

(defn prepare-record
  [params]
  (update params ::m.rates/rate double))

(s/fdef prepare-record
  :args (s/cat :params ::m.rates/params)
  :ret ::m.rates/params)

(defn create-record
  [params]
  (let [tempid (d/tempid "rate-id")
        prepared-params (-> (prepare-record params)
                            (assoc :db/id tempid)
                            (update ::m.rates/date tick/inst))
        response (d/transact db/*conn* {:tx-data [prepared-params]})
        id (get-in response [:tempids tempid])]
    (ms/put! streams/message-source [::create-record [:dinsro.events.rates/add-record id]])
    id))

(s/fdef create-record
  :args (s/cat :params ::m.rates/params)
  :ret ::ds/id)

(defn read-record
  [id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.rates/rate)
      (update record ::m.rates/date tick/instant))))

(s/fdef read-record
  :args (s/cat :id ::ds/id)
  :ret  (s/nilable ::m.rates/item))

(defn index-ids
  []
  (map first (d/q '[:find ?e :where [?e ::m.rates/rate _]] @db/*conn*)))

(s/fdef index-ids
  :args (s/cat)
  :ret (s/coll-of ::ds/id))

(defn index-records
  []
  (->> (index-ids)
       (d/pull-many @db/*conn* '[*])
       (sort-by ::m.rates/date)
       (reverse)
       (take record-limit)
       (map #(update % ::m.rates/date tick/instant))))

(s/fdef index-records
  :args (s/cat)
  :ret (s/coll-of ::m.rates/item))

(defn index-records-by-currency
  [currency-id]
  (->> (d/q {:query '[:find ?date ?rate
                      :in $ ?currency
                      :where
                      [?e ::m.rates/currency ?currency]
                      [?e ::m.rates/rate ?rate]
                      [?e ::m.rates/date ?date]]
             :args [@db/*conn* currency-id]})
       (sort-by first)
       (reverse)
       (take record-limit)
       (map (fn [[date rate]] [(.getTime date) rate]))))

(s/fdef index-records-by-currency
  :args (s/cat :currency-id ::ds/id)
  :ret ::m.rates/rate-feed)

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
