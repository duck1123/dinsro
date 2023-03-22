(ns dinsro.queries.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [tick.alpha.api :as tick]
   [xtdb.api :as xt]))

(>defn find-by-account
  [id]
  [::m.accounts/id => (s/coll-of ::m.transactions/id)]
  (log/info :find-by-account/starting {:account-id id})
  (c.xtdb/query-ids
   '{:find  [?id]
     :in    [[?account-id]]
     :where [[?id ::m.transactions/account ?account-id]]}
   [id]))

(>defn find-by-category
  [id]
  [::m.categories/id => (s/coll-of ::m.transactions/id)]
  (c.xtdb/query-ids
   '{:find  [?transaction-id]
     :in    [[?category-id]]
     :where [[?transaction-id ::m.transactions/category ?category-id]]}
   [id]))

(>defn find-by-currency
  [id]
  [::m.currencies/id => (s/coll-of ::m.transactions/id)]
  (c.xtdb/query-ids
   '{:find  [?transaction-id]
     :in    [[?user-id]]
     :where [[?transaction-id ::m.transactions/currency ?user-id]]}
   [id]))

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.transactions/id)]
  (log/info :find-by-user/starting {:user-id user-id})
  (c.xtdb/query-ids
   '{:find  [?transaction-id]
     :in    [[?user-id]]
     :where [[?debit-id ::m.debits/account ?account-id]
             [?debit-id ::m.debits/transaction ?transaction-id]
             [?account-id ::m.accounts/user ?user-id]]}
   [user-id]))

(>defn create-record
  [params]
  [::m.transactions/params => :xt/id]
  (log/info :create-record/starting {:params params})
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
    (when (get record ::m.transactions/id)
      (-> record
          (update ::m.transactions/date tick/instant)
          (dissoc :xt/id)))))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (c.xtdb/query-ids '{:find [?e] :where [[?e ::m.transactions/id _]]}))

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
