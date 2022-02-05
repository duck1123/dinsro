(ns dinsro.queries.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.specs]
   [xtdb.api :as xt]))

(def attributes-list
  '[:xt/id
    ::m.rate-sources/id
    ::m.rate-sources/name])
(def record-limit 1000)

(def find-dbid-by-id-query
  '[:find ?dbid
    :where [?dbid ::m.rate-sources/id _]])

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.rate-sources/id ?id]])

(def find-eid-by-name-query
  '[:find  ?eid
    :in    $ ?name
    :where [?eid ::m.rate-sources/name ?name]])

(def find-id-by-eid-query
  '[:find  ?id
    :in    $ ?eid
    :where [?eid ::m.rate-sources/id ?id]])

(>defn find-eid-by-id
  [id]
  [::m.rate-sources/id => :xt/id]
  (let [db (c.xtdb/main-db)]
    (ffirst (xt/q db find-eid-by-id-query id))))

(>defn find-eid-by-name
  [name]
  [::m.rate-sources/name => ::m.rate-sources/id]
  (let [db (c.xtdb/main-db)]
    (ffirst (xt/q db find-eid-by-name-query name))))

(>defn find-id-by-eid
  [eid]
  [:xt/id => ::m.rate-sources/id]
  (let [db (c.xtdb/main-db)]
    (ffirst (xt/q db find-id-by-eid-query eid))))

(>defn index-ids-by-account
  [account-id]
  [::m.accounts/id => (s/coll-of ::m.rate-sources/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?rate-source-id]
                :in    [?account-id]
                :where [[?rate-source-id ::m.rate-sources/account ?account-id]]}]
    (map first (xt/q db query account-id))))

(>defn index-ids-by-currency
  [currency-id]
  [::m.currencies/id => (s/coll-of ::m.rate-sources/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?rate-source-id]
                :in    [?currency-id]
                :where [[?rate-source-id ::m.rate-sources/currency ?currency-id]]}]
    (map first (xt/q db query currency-id))))

(>defn find-id-by-currency-and-name
  [currency-id name]
  [::m.rate-sources/currency ::m.rate-sources/name => (? ::m.rate-sources/id)]
  (let [db (c.xtdb/main-db)
        query '{:find  [?rate-source-id]
                :in    [?currency-id ?name]
                :where [[?rate-source-id ::m.rate-sources/currency ?currency-id]
                        [?rate-source-id ::m.rate-sources/name ?name]]}]
    (ffirst (xt/q db query currency-id name))))

(>defn create-record
  [params]
  [::m.rate-sources/params => :xt/id]
  (let [node   (c.xtdb/main-node)
        id     (new-uuid)
        params (assoc params ::m.rate-sources/id id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.rate-sources/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.rate-sources/name)
      record)))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (let [db (c.xtdb/main-db)]
    (map first (xt/q db '{:find  [?e]
                          :where [[?e ::m.rate-sources/name _]]}))))

(>defn index-records
  []
  [=> (s/coll-of ::m.rate-sources/item)]
  (map read-record (index-ids)))

(defn index-records-by-currency
  [currency-id]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id ?currency-id]
                :keys  [xt/id name]
                :in    [$ ?currency-id]
                :where [[?id ::m.rate-sources/currency ?currency-id]]}]
    (->> (xt/q db query currency-id)
         (map :xt/id)
         (map read-record)
         (take record-limit))))

(>defn delete-record
  [id]
  [:xt/id => any?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))

(comment
  (index-ids)
  (index-records)
  (index-records-by-currency 408231720)
  (delete-all))
