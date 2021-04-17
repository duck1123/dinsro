(ns dinsro.queries.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.users :as q.users]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as timbre]))

(def attribute-list
  '[:db/id
    ::m.accounts/currency
    ::m.accounts/id
    ::m.accounts/initial-value
    ::m.accounts/name
    ::m.accounts/user])
(def record-limit 1000)

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.accounts/id ?id]])

(def find-id-by-eid-query
  '[:find  ?id
    :in    $ ?eid
    :where [?eid ::m.accounts/id ?id]])

(>defn find-eid-by-id
  [id]
  [::m.accounts/id => :db/id]
  (ffirst (d/q find-eid-by-id-query @db/*conn* id)))

(>defn find-id-by-eid
  [eid]
  [:db/id => ::m.accounts/id]
  (ffirst (d/q find-id-by-eid-query @db/*conn* eid)))

(>defn create-record
  [params]
  [::m.accounts/params => :db/id]
  (let [params   (assoc params ::m.accounts/id (utils/uuid))
        params   (assoc params :db/id "account-id")
        response (d/transact db/*conn* {:tx-data [params]})]
    (get-in response [:tempids "account-id"])))

(>defn read-record
  [id]
  [:db/id => (? ::m.accounts/item)]
  (let [record (d/pull @db/*conn* attribute-list id)]
    (when (get record ::m.accounts/name)
      (let [user-id (get-in record [::m.accounts/user :db/id])
            currency-id (get-in record [::m.accounts/currency :db/id])]
        (-> record
            (dissoc :db/id)
            (update ::m.accounts/currency dissoc :db/id)
            (update ::m.accounts/user dissoc :db/id)
            (assoc-in [::m.accounts/currency ::m.currencies/id]
                      (q.currencies/find-id-by-eid currency-id))
            (assoc-in [::m.accounts/user ::m.users/id]
                      (q.users/find-id-by-eid user-id)))))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (map first (d/q '[:find ?e :where [?e ::m.accounts/name _]] @db/*conn*)))

(>defn index-records
  []
  [=> (s/coll-of ::m.accounts/item)]
  (map read-record (index-ids)))

(>defn index-records-by-currency
  [currency-id]
  [:db/id => (s/coll-of ::m.accounts/item)]
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

(>defn index-records-by-user
  [user-id]
  [:db/id => (s/coll-of ::m.accounts/item)]
  (->> (d/q {:query '[:find
                      ?id
                      ?user-id
                      :keys db/id name
                      :in $ ?user-id
                      :where
                      [?id ::m.accounts/user ?user-id]]
             :args  [@db/*conn* user-id]})
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
