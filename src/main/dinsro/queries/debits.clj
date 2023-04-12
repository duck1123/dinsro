(ns dinsro.queries.debits
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.users :as m.users]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../../notebooks/dinsro/notebooks/debits.clj]]
;; [[../model/debits.cljc][Model]]

(>defn create-record
  [params]
  [::m.debits/params => ::m.debits/id]
  (log/info :create-record/starting {:params params})
  (let [id     (new-uuid)
        node   (c.xtdb/main-node)
        params (assoc params ::m.debits/id id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    (log/trace :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [::m.debits/id => (? ::m.debits/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.debits/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.debits/id)]
  (c.xtdb/query-ids '{:find [?e] :where [[?e ::m.debits/id _]]}))

(>defn delete!
  [id]
  [::m.debits/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn find-by-account
  [account-id]
  [::m.accounts/id => (s/coll-of ::m.debits/id)]
  (log/info :find-by-account/starting {:account-id account-id})
  (c.xtdb/query-ids
   '{:find  [?debit-id]
     :in    [[?account-id]]
     :where [[?debit-id ::m.debits/account ?account-id]]}
   [account-id]))

(>defn find-by-transaction
  [transaction-id]
  [::m.debits/transaction => (s/coll-of ::m.debits/id)]
  (log/info :find-by-transaction/starting {:transaction-id transaction-id})
  (c.xtdb/query-ids
   '{:find  [?debit-id]
     :in    [[?transaction-id]]
     :where [[?debit-id ::m.debits/transaction ?transaction-id]]}
   [transaction-id]))

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.debits/id)]
  (log/info :find-by-user/starting {:user-id user-id})
  (c.xtdb/query-ids
   '{:find  [?debit-id]
     :in    [[?transaction-id]]
     :where [[?debit-id ::m.debits/account ?account-id]
             [?account-id ::m.accounts/user ?user-id]]}
   [user-id]))
