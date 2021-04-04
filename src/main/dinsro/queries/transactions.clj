(ns dinsro.queries.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(>defn create-record
  [params]
  [::m.transactions/params => :db/id]
  (let [tempid          (d/tempid "transaction-id")
        prepared-params (-> params
                            (assoc  :db/id tempid)
                            (update ::m.transactions/date tick/inst))
        response        (d/transact db/*conn* {:tx-data [prepared-params]})]
    (get-in response [:tempids tempid])))

(>defn read-record
  [id]
  [:db/id => (? ::m.transactions/item)]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.transactions/value)
      (update record ::m.transactions/date tick/instant))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (map first (d/q '[:find ?e :where [?e ::m.transactions/value _]] @db/*conn*)))

(>defn index-records
  []
  [=> (s/coll-of ::m.transactions/item)]
  (->> (index-ids)
       (d/pull-many @db/*conn* '[*])
       (map #(update % ::m.transactions/date tick/instant))))

(>defn delete-record
  [id]
  [:db/id => nil?]
  (do
    (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
