(ns dinsro.queries.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]))

(def record-limit 1000)

(>defn create-record
  [params]
  [::m.accounts/params => ::ds/id]
  (let [response (d/transact db/*conn* {:tx-data [(assoc params :db/id "account-id")]})]
    (get-in response [:tempids "account-id"])))

(>defn read-record
  [id]
  [::ds/id => (? ::m.accounts/item)]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.accounts/name)
      record)))

(>defn index-ids
  []
  [=> (s/coll-of ::ds/id)]
  (map first (d/q '[:find ?e :where [?e ::m.accounts/name _]] @db/*conn*)))

(>defn index-records
  []
  [=> (s/coll-of ::m.accounts/item)]
  (d/pull-many @db/*conn* '[*] (index-ids)))

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
             :args [@db/*conn* user-id]})
       (map :db/id)
       (map read-record)
       (take record-limit)))

(>defn delete-record
  [id]
  [::ds/id => nil?]
  (do
    (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
