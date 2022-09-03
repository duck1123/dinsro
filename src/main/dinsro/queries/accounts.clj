(ns dinsro.queries.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [xtdb.api :as xt]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(def record-limit 1000)

(>defn find-by-user-and-name
  [user-id name]
  [::m.accounts/user ::m.accounts/name => ::m.accounts/id]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?account-id]
                :in    [?user-id ?name]
                :where [[?account-id ::m.accounts/name ?name]
                        [?account-id ::m.accounts/user ?user-id]]}]
    (ffirst (xt/q db query user-id name))))

(>defn find-by-currency
  [currency-id]
  [::m.currencies/id => (s/coll-of ::m.accounts/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?account-id]
                :in    [[?currency-id]]
                :where [[?account-id ::m.accounts/currency ?currency-id]]}]
    (map first (xt/q db query [currency-id]))))

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.accounts/id)]
  (log/debug :accounts/find-by-user {:user-id user-id})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?account-id]
                :in    [?user-id]
                :where [[?account-id ::m.accounts/user ?user-id]]}]
    (map first (xt/q db query user-id))))

(>defn create-record
  [params]
  [::m.accounts/params => :xt/id]
  (let [id       (new-uuid)
        node     (c.xtdb/main-node)
        params   (assoc params ::m.accounts/id id)
        params   (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.accounts/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.accounts/name)
      record)))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (let [db    (c.xtdb/main-db)
        query '[:find ?e :where [?e ::m.accounts/name _]]]
    (map first (xt/q db query))))

(>defn index-records
  []
  [=> (s/coll-of ::m.accounts/item)]
  (map read-record (index-ids)))

(>defn index-records-by-currency
  [currency-id]
  [:xt/id => (s/coll-of ::m.accounts/item)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id ?currency-id]
                :keys  [xt/id name]
                :in    [?currency-id]
                :where [[?id ::m.accounts/currency ?currency-id]]}]
    (->> (xt/q db query currency-id)
         (map :xt/id)
         (map read-record)
         (take record-limit))))

(>defn index-records-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.accounts/item)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?account-id]
                :in    [?user-id]
                :where [[?account-id ::m.accounts/user ?user-id]]}]
    (->> (xt/q db query user-id)
         (map first)
         (map read-record)
         (take record-limit))))

(>defn delete!
  [id]
  [::m.accounts/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete! id)))
