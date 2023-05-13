(ns dinsro.queries.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(def record-limit 1000)

(>defn find-by-user-and-name
  [user-id name]
  [::m.accounts/user ::m.accounts/name => ::m.accounts/id]
  (c.xtdb/query-value
   '{:find  [?account-id]
     :in    [[?user-id ?name]]
     :where [[?account-id ::m.accounts/name ?name]
             [?account-id ::m.accounts/user ?user-id]]}
   [user-id name]))

(>defn find-by-currency
  [currency-id]
  [::m.currencies/id => (s/coll-of ::m.accounts/id)]
  (c.xtdb/query-values
   '{:find  [?account-id]
     :in    [[?currency-id]]
     :where [[?account-id ::m.accounts/currency ?currency-id]]}
   [currency-id]))

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.accounts/id)]
  (log/debug :accounts/find-by-user {:user-id user-id})
  (c.xtdb/query-values
   '{:find  [?account-id]
     :in    [[?user-id]]
     :where [[?account-id ::m.accounts/user ?user-id]]}
   [user-id]))

(>defn create-record
  [params]
  [::m.accounts/params => :xt/id]
  (let [id       (new-uuid)
        node     (c.xtdb/get-node)
        params   (assoc params ::m.accounts/id id)
        params   (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.accounts/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.accounts/name)
      record)))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (c.xtdb/query-values '{:find [?e] :where [[?e ::m.accounts/name _]]}))

(>defn index-records
  []
  [=> (s/coll-of ::m.accounts/item)]
  (map read-record (index-ids)))

(>defn delete!
  [id]
  [::m.accounts/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(defn find-by-rate-source
  [rate-source-id]
  (log/trace :find-by-rate-source/starting {:rate-source-id rate-source-id})
  (c.xtdb/query-values
   '{:find  [?account-id]
     :in    [[?rate-source-id]]
     :where [[?account-id ::m.accounts/source ?rate-source-id]]}
   [rate-source-id]))

(defn find-by-wallet
  [wallet-id]
  (log/trace :find-by-wallet/starting {:wallet-id wallet-id})
  (c.xtdb/query-values
   '{:find  [?account-id]
     :in    [[?wallet-id]]
     :where [[?account-id ::m.accounts/wallet ?wallet-id]]}
   [wallet-id]))
