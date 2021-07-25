(ns dinsro.queries.currencies
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.components.datahike :as db]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [taoensso.timbre :as log]))

(def attribute-list
  '[:db/id
    ::m.currencies/id
    ::m.currencies/name])
(def record-limit 1000)

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.currencies/id ?id]])

(def find-id-by-eid-query
  '[:find  ?id
    :in    $ ?eid
    :where [?eid ::m.currencies/id ?id]])

(def find-name-by-eid-query
  '[:find  ?name
    :in    $ ?eid
    :where [?eid ::m.currencies/name ?name]])

(>defn find-eid-by-id
  [id]
  [::m.currencies/id => :db/id]
  (ffirst (d/q find-eid-by-id-query @db/*conn* id)))

(>defn find-name-by-eid
  [eid]
  [:db/id => ::m.currencies/name]
  (ffirst (d/q find-name-by-eid-query @db/*conn* eid)))

(>defn find-id-by-eid
  [eid]
  [:db/id => ::m.currencies/id]
  (ffirst (d/q find-id-by-eid-query @db/*conn* eid)))

(>defn create-record
  [params]
  [::m.currencies/params => :db/id]
  (try
    (let [params   (assoc params :db/id "currency-id")
          params   (dissoc params ::m.currencies/user)
          response (d/transact db/*conn* {:tx-data [params]})]
      (get-in response [:tempids "currency-id"]))
    (catch Exception ex
      (log/error ex "Error creating")
      nil)))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (map first (d/q '[:find ?e :where [?e ::m.currencies/name _]] @db/*conn*)))

(>defn read-record
  [id]
  [:db/id => (? ::m.currencies/item)]
  (let [record (d/pull @db/*conn* attribute-list id)]
    (when (get record ::m.currencies/name)
      (dissoc record :db/id))))

(>defn find-by-id
  [id]
  [::m.currencies/id => (? ::m.currencies/item)]
  (let [eid (find-eid-by-id id)
        record (d/pull @db/*conn* attribute-list eid)]
    (when (get record ::m.currencies/name)
      (dissoc record :db/id))))

(>defn index-records
  []
  [=> (s/coll-of ::m.currencies/item)]
  (map read-record (index-ids)))

(>defn index-by-user
  [_id]
  [::m.users/id => (s/coll-of ::m.currencies/item)]
  (map read-record (index-ids)))

(defn index-records-by-account
  [currency-id]
  (->> (d/q {:query '[:find
                      ?id
                      ?currency-id
                      :keys db/id name
                      :in $ ?currency-id
                      :where
                      [?id ::m.accounts/currency ?currency-id]]
             :args  [@db/*conn* currency-id]})
       (map :db/id)
       (map read-record)
       (take record-limit)))

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
