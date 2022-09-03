(ns dinsro.queries.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [tick.alpha.api :as tick]
   [xtdb.api :as xt]))

(def record-limit 75)

(>defn find-by-account
  [id]
  [::m.accounts/id => (s/coll-of ::m.transactions/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [?account-id]
                :where [[?id ::m.transactions/account ?account-id]]}]
    (map first (xt/q db query id))))

(>defn find-by-category
  [id]
  [::m.categories/id => (s/coll-of ::m.transactions/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?transaction-id]
                :in    [?category-id]
                :where [[?transaction-id ::m.transactions/category ?category-id]]}]
    (map first (xt/q db query id))))

(>defn find-by-currency
  [id]
  [::m.currencies/id => (s/coll-of ::m.transactions/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?transaction-id]
                :in    [?user-id]
                :where [[?transaction-id ::m.transactions/currency ?user-id]]}]
    (map first (xt/q db query id))))

(>defn find-by-user
  [id]
  [::m.users/id => (s/coll-of ::m.transactions/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?transaction-id]
                :in    [?user-id]
                :where [[?transaction-id ::m.transactions/account ?account-id]
                        [?account-id ::m.accounts/user ?user-id]]}]
    (map first (xt/q db query id))))

(>defn create-record
  [params]
  [::m.transactions/params => :xt/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.transactions/id id)
                            (assoc :xt/id id)
                            (update ::m.transactions/date tick/inst))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.transactions/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.transactions/value)
      (-> record
          (update ::m.transactions/date tick/instant)
          (dissoc :xt/id)))))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (let [db (c.xtdb/main-db)
        query '{:find [?e]
                :where [[?e ::m.transactions/value _]]}]
    (map first (xt/q db query))))

(>defn index-records
  []
  [=> (s/coll-of ::m.transactions/item)]
  (map read-record (index-ids)))

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
