(ns dinsro.model.rates
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec.rates :as s.rates]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]
            [tick.alpha.api :as tick]))

(defn-spec prepare-record ::s.rates/params
  [params ::s.rates/params]
  (let [rate (::s.rates/rate params)]
    (update params ::s.rates/rate double)))

(defn-spec create-record ::ds/id
  [params ::s.rates/params]
  (let [tempid (d/tempid "rate-id")
        prepared-params (-> (prepare-record params)
                            (assoc :db/id tempid)
                            (update ::s.rates/date tick/inst))
        response (d/transact db/*conn* {:tx-data [prepared-params]})]
    (get-in response [:tempids tempid])))

(defn-spec read-record (s/nilable ::s.rates/item)
  [id ::ds/id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::s.rates/rate)
      (update record ::s.rates/date tick/instant))))

(defn-spec index-ids (s/coll-of ::ds/id)
  []
  (map first (d/q '[:find ?e :where [?e ::s.rates/rate _]] @db/*conn*)))

(defn-spec index-records (s/coll-of ::s.rates/item)
  []
  (->> (index-ids)
       (take 20)
       (d/pull-many @db/*conn* '[*])
       (map #(update % ::s.rates/date tick/instant))))

(defn-spec delete-record nil?
  [id ::ds/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))
