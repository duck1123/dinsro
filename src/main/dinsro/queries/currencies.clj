(ns dinsro.queries.currencies
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
    ::m.currencies/id
    ::m.currencies/name])
(def record-limit 1000)

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.currencies/id ?id]])

(def find-eid-by-code-query
  '{:find  [?eid]
    :in    [?code]
    :where [[?eid ::m.currencies/code ?code]]})

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
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-eid-by-id-query id))))

(>defn find-eid-by-code
  [id]
  [::m.currencies/code => ::m.currencies/id]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-eid-by-code-query id))))

(>defn find-name-by-eid
  [eid]
  [:db/id => ::m.currencies/name]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-name-by-eid-query eid))))

(>defn find-id-by-eid
  [eid]
  [:db/id => (? ::m.currencies/id)]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-id-by-eid-query eid))))

(>defn create-record
  [params]
  [::m.currencies/params => (? ::m.currencies/id)]
  (try
    (let [node   (c.crux/main-node)
          id     (new-uuid)
          params (assoc params :crux.db/id id)
          params (assoc params ::m.currencies/id id)
          params (dissoc params ::m.currencies/user)]
      (crux/await-tx node (crux/submit-tx node [[:crux.tx/put params]]))
      id)
    (catch Exception ex
      (log/error ex "Error creating")
      nil)))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?e]
                :where [[?e ::m.currencies/name _]]}]
    (map first (crux/q db query))))

(>defn read-record
  [id]
  [:db/id => (? ::m.currencies/item)]
  (let [db (c.crux/main-db)
        record (crux/pull db '[*] id)]
    (when (get record ::m.currencies/name)
      (dissoc record :db/id))))

(>defn find-by-id
  [id]
  [::m.currencies/id => (? ::m.currencies/item)]
  (let [db     (c.crux/main-db)
        eid    (find-eid-by-id id)
        record (crux/pull db attribute-list eid)]
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
  (let [db    (c.crux/main-db)
        query '{:find  [?id ?currency-id]
                :keys  [db/id name]
                :in    [?currency-id]
                :where [[?id ::m.accounts/currency ?currency-id]]}]
    (->> (crux/q db query currency-id)
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
