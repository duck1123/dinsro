(ns dinsro.queries.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as log]
   [tick.alpha.api :as tick]))

(def record-limit 75)

(def find-eid-by-account-query
  '{:find  [?eid]
    :in    [?account-id]
    :where [[?eid ::m.transactions/account ?account-id]]})

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.transactions/id ?id]])

(def find-id-by-eid-query
  '[:find  ?id
    :in    $ ?eid
    :where [?eid ::m.transactions/id ?id]])

(>defn find-by-account
  [id]
  [::m.accounts/id => (s/coll-of ::m.transactions/id)]
  (let [db (c.crux/main-db)]
    (map first (crux/q db find-eid-by-account-query id))))

(>defn find-by-currency
  [id]
  [::m.users/id => (s/coll-of ::m.transactions/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?transaction-id]
                :in    [?user-id]
                :where [[?transaction-id ::m.transactions/currency ?user-id]]}]
    (map first (crux/q db query id))))

(>defn find-by-user
  [id]
  [::m.users/id => (s/coll-of ::m.transactions/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?transaction-id]
                :in    [?user-id]
                :where [[?transaction-id ::m.transactions/account ?account-id]
                        [?account-id ::m.accounts/user ?user-id]]}]
    (map first (crux/q db query id))))

(>defn find-eid-by-id
  [id]
  [::m.transactions/id => :db/id]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-eid-by-id-query id))))

(>defn find-id-by-eid
  [eid]
  [:db/id => ::m.transactions/id]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-id-by-eid-query eid))))

(>defn create-record
  [params]
  [::m.transactions/params => :db/id]
  (let [node            (c.crux/main-node)
        id              (utils/uuid)
        prepared-params (-> params
                            (assoc ::m.transactions/id id)
                            (assoc :crux.db/id id)
                            (update ::m.transactions/date tick/inst))]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [:db/id => (? ::m.transactions/item)]
  (let [db     (c.crux/main-db)
        record (crux/pull db '[*] id)]
    (when (get record ::m.transactions/value)
      (-> record
          (update ::m.transactions/date tick/instant)
          (dissoc :db/id)))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (let [db (c.crux/main-db)
        query '{:find [?e]
                :where [[?e ::m.transactions/value _]]}]
    (map first (crux/q db query))))

(>defn index-records
  []
  [=> (s/coll-of ::m.transactions/item)]
  (map read-record (index-ids)))

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
