(ns dinsro.queries.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(def query-info
  {:ident    ::m.accounts/id
   :pk       '?account-id
   :clauses  [[:actor/id           '?actor-id]
              [:actor/admin?       '?admin?]
              [::m.rate-sources/id '?rate-source-id]
              [::m.c.wallets/id    '?wallet-id]
              [::m.currencies/id   '?currency-id]
              [::m.rates/id        '?rate-id]]
   :order-by [['?account-date :desc]]
   ;; :additional-clauses ['?account-date]
   ;; :additional-rules [['?account-id ::m.accounts/date '?account-date]]
   :rules
   (fn [[actor-id admin? rate-source-id wallet-id currency-id rate-id] rules]
     (->> rules
          (concat-when (and (not admin?) actor-id)
            [['?account-id ::m.accounts/user     '?actor-id]])
          (concat-when rate-source-id
            [['?account-id ::m.accounts/source   '?rate-source-id]])
          (concat-when wallet-id
            [['?account-id ::m.accounts/wallet   '?wallet-id]])
          (concat-when currency-id
            [['?account-id ::m.accounts/currency '?currency-id]])
          (concat-when rate-id
            [['?account-id ::m.accounts/source   '?rate-rate-source-id]
             ['?rate-id    ::m.rates/source      '?rate-rate-source-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(def record-limit 1000)
(def pk '?account-id)
(def ident-key ::m.categories/id)
(def index-param-syms ['?user-id])

(>def ::query-params (s/keys))

(defn get-index-params
  [query-params]
  (let [{user-id ::m.users/id} query-params]
    [user-id]))

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
