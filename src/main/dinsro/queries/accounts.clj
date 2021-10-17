(ns dinsro.queries.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [taoensso.timbre :as log]))

(def attribute-list
  '[:db/id
    ::m.accounts/currency
    ::m.accounts/id
    ::m.accounts/initial-value
    ::m.accounts/name
    ::m.accounts/user])
(def record-limit 1000)

(def find-eid-by-id-query
  '{:find  [?eid]
    :in    [?id]
    :where [[?eid ::m.accounts/id ?id]]})

(def find-id-by-eid-query
  '{:find  [?id]
    :in    [?eid]
    :where [[?eid ::m.accounts/id ?id]]})

(>defn find-eid-by-id
  [id]
  [::m.accounts/id => :db/id]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-eid-by-id-query id))))

(>defn find-id-by-eid
  [eid]
  [:db/id => ::m.accounts/id]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-id-by-eid-query eid))))

(>defn find-id-by-user-and-name
  [user-id name]
  [::m.accounts/user ::m.accounts/name => ::m.accounts/id]
  (let [db    (c.crux/main-db)
        query '{:find  [?account-id]
                :in    [?user-id ?name]
                :where [[?account-id ::m.accounts/name ?name]
                        [?account-id ::m.accounts/user ?user-id]]}]
    (ffirst (crux/q db query user-id name))))

(>defn find-by-currency
  [currency-id]
  [::m.currencies/id => (s/coll-of ::m.accounts/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?account-eid]
                :in    [?currency-id]
                :where [[?account-eid ::m.accounts/currency ?currency-id]]}]
    (map first (crux/q db query currency-id))))

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.accounts/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?account-eid]
                :in    [?users-id]
                :where [[?account-eid ::m.accounts/user ?user-id]]}]
    (map first (crux/q db query user-id))))

(>defn create-record
  [params]
  [::m.accounts/params => :db/id]
  (let [id       (new-uuid)
        node     (c.crux/main-node)
        params   (assoc params ::m.accounts/id id)
        params   (assoc params :crux.db/id id)]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/put params]]))
    id))

(>defn read-record
  [id]
  [:db/id => (? ::m.accounts/item)]
  (let [db     (c.crux/main-db)
        record (crux/pull db '[*] id)]
    (when (get record ::m.accounts/name)
      record)))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (let [db    (c.crux/main-db)
        query '[:find ?e :where [?e ::m.accounts/name _]]]
    (map first (crux/q db query))))

(>defn index-records
  []
  [=> (s/coll-of ::m.accounts/item)]
  (map read-record (index-ids)))

(>defn index-records-by-currency
  [currency-id]
  [:db/id => (s/coll-of ::m.accounts/item)]
  (let [db    (c.crux/main-db)
        query '{:find  [?id ?currency-id]
                :keys  [db/id name]
                :in    [?currency-id]
                :where [[?id ::m.accounts/currency ?currency-id]]}]
    (->> (crux/q db query currency-id)
         (map :db/id)
         (map read-record)
         (take record-limit))))

(>defn index-records-by-user
  [user-id]
  [:db/id => (s/coll-of ::m.accounts/item)]
  (let [db    (c.crux/main-db)
        query '{:find  [?id ?user-id]
                :keys  [db/id name]
                :in    [?user-eid]
                :where [[?id ::m.accounts/user ?user-id]
                        [?user-eid ::m.users/id ?user-id]]}]
    (->> (crux/q db query user-id)
         (map :db/id)
         (map read-record)
         (take record-limit))))

(>defn delete-record
  [id]
  [:db/id => nil?]
  (let [node (c.crux/main-node)]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/delete id]]))
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
