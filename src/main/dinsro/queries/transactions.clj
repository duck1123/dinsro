(ns dinsro.queries.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.components.datahike :as db]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(def record-limit 75)

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.transactions/id ?id]])

(def find-id-by-eid-query
  '[:find  ?id
    :in    $ ?eid
    :where [?eid ::m.transactions/id ?id]])

(>defn find-eid-by-id
  [id]
  [::m.transactions/id => :db/id]
  (ffirst (d/q find-eid-by-id-query @db/*conn* id)))

(>defn find-id-by-eid
  [eid]
  [:db/id => ::m.transactions/id]
  (ffirst (d/q find-id-by-eid-query @db/*conn* eid)))

(>defn create-record
  [params]
  [::m.transactions/params => :db/id]
  (let [tempid          (d/tempid "transaction-id")
        prepared-params (-> params
                            (assoc  ::m.transactions/id (str (utils/uuid)))
                            (assoc  :db/id tempid)
                            (update ::m.transactions/date tick/inst))
        response        (d/transact db/*conn* {:tx-data [prepared-params]})]
    (get-in response [:tempids tempid])))

(>defn read-record
  [id]
  [:db/id => (? ::m.transactions/item)]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.transactions/value)
      (let [account-id (get-in record [::m.transactions/account :db/id])]
        (-> record
            (update ::m.transactions/date tick/instant)
            (dissoc :db/id)
            (assoc-in [::m.transactions/account ::m.accounts/id]
                      (q.accounts/find-id-by-eid account-id))
            (update ::m.transactions/account dissoc :db/id))))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (map first (d/q '[:find ?e :where [?e ::m.transactions/value _]] @db/*conn*)))

(>defn index-records
  []
  [=> (s/coll-of ::m.transactions/item)]
  (map read-record (index-ids)))

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
