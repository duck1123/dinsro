(ns dinsro.queries.currencies
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [io.pedestal.log :as log]
   [xtdb.api :as xt]))

(def record-limit 1000)

(>defn find-by-code
  [code]
  [::m.currencies/code => ::m.currencies/id]
  (let [db (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [[?code]]
                :where [[?id ::m.currencies/code ?code]]}]
    (ffirst (xt/q db query [code]))))

(>defn find-by-debit
  [debit-id]
  [::m.debits/id => (? ::m.currencies/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?currency-id]
                :in    [[?debit-id]]
                :where [[?debit-id ::m.debits/account ?account-id]
                        [?account-id ::m.accounts/currency ?currency-id]]}]
    (ffirst (xt/q db query [debit-id]))))

(>defn find-name-by-id
  [id]
  [:xt/id => ::m.currencies/name]
  (let [db (c.xtdb/main-db)
        query '{:find  [?name]
                :in    [[?id]]
                :where [[?id ::m.currencies/name ?name]]}]
    (ffirst (xt/q db query [id]))))

(>defn find-by-user-and-name
  [user-id name]
  [::m.users/id ::m.currencies/name => (? ::m.currencies/id)]
  (let [db (c.xtdb/main-db)
        query '{:find  [?currency-id]
                :in    [[?user-id ?name]]
                :where [[?currency-id ::m.currencies/user ?user-id]
                        [?currency-id ::m.currencies/name ?name]]}]
    (ffirst (xt/q db query [user-id name]))))

(>defn create-record
  [params]
  [::m.currencies/params => (? ::m.currencies/id)]
  (try
    (let [node   (c.xtdb/main-node)
          id     (new-uuid)
          params (assoc params :xt/id id)
          params (assoc params ::m.currencies/id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)
    (catch Exception ex
      (log/error :create/failed {:exception ex})
      nil)))

(>defn index-ids
  []
  [=> (s/coll-of ::m.currencies/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.currencies/name _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [::m.currencies/id => (? ::m.currencies/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.currencies/id)
      (dissoc record :xt/id))))

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
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id ?currency-id]
                :keys  [xt/id name]
                :in    [?currency-id]
                :where [[?id ::m.accounts/currency ?currency-id]]}]
    (->> (xt/q db query currency-id)
         (map :xt/id)
         (map read-record)
         (take record-limit))))

(>defn delete-record
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
