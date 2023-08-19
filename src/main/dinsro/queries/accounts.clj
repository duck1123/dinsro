(ns dinsro.queries.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(def model-key ::m.accounts/id)

(def query-info
  {:ident    model-key
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

(>def ::query-params (s/keys))

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
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [:xt/id => (? ::m.accounts/item)]
  (c.xtdb/read model-key id))

(>defn delete!
  [id]
  [::m.accounts/id => nil?]
  (c.xtdb/delete! id))
