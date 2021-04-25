(ns dinsro.queries.categories
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.components.datahike :as db]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as timbre]))

(def attributes-list
  '[:db/id
    ::m.categories/id
    ::m.categories/name])
(def record-limit 1000)

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.categories/id ?id]])

(def find-id-by-eid-query
  '[:find  ?id
    :in    $ ?eid
    :where [?eid ::m.categories/id ?id]])

(>defn find-eid-by-id
  [id]
  [::m.categories/id => :db/id]
  (ffirst (d/q find-eid-by-id-query @db/*conn* id)))

(>defn find-id-by-eid
  [eid]
  [:db/id => ::m.categories/id]
  (ffirst (d/q find-id-by-eid-query @db/*conn* eid)))

(>defn create-record
  [params]
  [::m.categories/params => :db/id]
  (let [params   (assoc params ::m.categories/id (utils/uuid))
        params   (assoc params :db/id "record-id")
        response (d/transact db/*conn* {:tx-data [params]})]
    (get-in response [:tempids "record-id"])))

(>defn read-record
  [id]
  [:db/id => (? ::m.categories/item)]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.categories/name)
      (let [user-eid (get-in record [::m.categories/user :db/id])
            user-id (q.users/find-id-by-eid user-eid)]
        (-> record
            (dissoc :db/id)
            (assoc ::m.categories/user {::m.users/id user-id}))))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (map first (d/q '[:find ?e :where [?e ::m.categories/name _]] @db/*conn*)))

(>defn index-records
  []
  [=> (s/coll-of ::m.categories/item)]
  (d/pull-many @db/*conn* '[*] (index-ids)))

(>defn delete-record
  [id]
  [:db/id => nil?]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
