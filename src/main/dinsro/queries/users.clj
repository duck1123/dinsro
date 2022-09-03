(ns dinsro.queries.users
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [xtdb.api :as xt]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.specs]))

(>defn read-record
  [user-id]
  [uuid? => (? ::m.users/item)]
  (let [db     (c.xtdb/main-db)
        query  '{:find  [(pull ?id [*])]
                 :in    [[?id]]
                 :where [[?id ::m.users/id ?name]]}
        result (xt/q db query [user-id])
        record (ffirst result)]
    (when (get record ::m.users/id)
      (dissoc record :xt/id))))

(>defn read-records
  [ids]
  [(s/coll-of :xt/id) => (s/coll-of ::m.users/item)]
  (map read-record ids))

(>defn find-by-name
  [name]
  [::m.users/name => (? ::m.users/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [[?name]]
                :where [[?id ::m.users/name ?name]]}]
    (ffirst (xt/q db query [name]))))

(>defn find-by-transaction
  [transaction-id]
  [::m.transactions/name => (? ::m.users/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?user-id]
                :in    [[?transaction-id]]
                :where [[?transaction-id ::m.transactions/account ?account-id]
                        [?account-id ::m.accounts/user ?user-id]]}]
    (ffirst (xt/q db query [transaction-id]))))

(>defn create-record
  "Create a user record"
  [params]
  [::m.users/params => ::m.users/id]
  (if (nil? (find-by-name (::m.users/name params)))
    (let [node   (c.xtdb/main-node)
          id     (new-uuid)
          params (assoc params :xt/id id)
          params (assoc params ::m.users/id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)
    (throw (RuntimeException. "User already exists"))))

(>defn index-ids
  "list all user ids"
  []
  [=> (s/coll-of ::m.users/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.users/id _]]}]
    (map first (xt/q db query))))

(>defn index-records
  "list all users"
  []
  [=> (s/coll-of ::m.users/item)]
  (read-records (index-ids)))

(>defn delete-record
  "delete user by id"
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all
  "delete all users"
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
