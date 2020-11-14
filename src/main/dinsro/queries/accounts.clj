(ns dinsro.queries.accounts
  (:require
   [clojure.spec.alpha :as s]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]))

(def record-limit 1000)

(defn create-record
  [params]
  (let [response (d/transact db/*conn* {:tx-data [(assoc params :db/id "account-id")]})]
    (get-in response [:tempids "account-id"])))

(s/fdef create-record
  :args (s/cat :params ::m.accounts/params)
  :ret ::ds/id)

(defn read-record
  [id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::m.accounts/name)
      record)))

(s/fdef read-record
  :args (s/cat :id ::ds/id)
  :ret  (s/nilable ::m.accounts/item))

(defn index-ids
  []
  (map first (d/q '[:find ?e :where [?e ::m.accounts/name _]] @db/*conn*)))

(s/fdef index-ids
  :args (s/cat)
  :ret (s/coll-of ::ds/id))

(defn index-records
  []
  (d/pull-many @db/*conn* '[*] (index-ids)))

(s/fdef index-records
  :args (s/cat)
  :ret (s/coll-of ::m.accounts/item))

(defn index-records-by-user
  [user-id]
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

(s/fdef index-records-by-user
  :args (s/cat :user-id :db/id)
  :ret (s/coll-of ::m.accounts/item))

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
