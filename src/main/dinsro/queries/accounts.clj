(ns dinsro.queries.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.users :as m.users]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.core.wallets :as o.c.wallets]
   [dinsro.options.currencies :as o.currencies]
   [dinsro.options.rate-sources :as o.rate-sources]
   [dinsro.options.rates :as o.rates]
   [dinsro.options.users :as o.users]
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../../test/dinsro/queries/accounts_test.clj]]

(def model-key ::m.accounts/id)

(def query-info
  {:ident    model-key
   :pk       '?account-id
   :clauses  [[:actor/id         '?actor-id]
              [:actor/admin?     '?admin?]
              [o.rate-sources/id '?rate-source-id]
              [o.c.wallets/id    '?wallet-id]
              [o.currencies/id   '?currency-id]
              [o.rates/id        '?rate-id]
              [o.users/id        '?user-id]]
   :order-by [['?account-date :desc]]
   :rules
   (fn [[actor-id admin? rate-source-id wallet-id currency-id rate-id user-id] rules]
     (->> rules
          (concat-when (and (not admin?) actor-id)
            [['?account-id o.accounts/user     '?actor-id]])
          (concat-when rate-source-id
            [['?account-id o.accounts/source   '?rate-source-id]])
          (concat-when wallet-id
            [['?account-id o.accounts/wallet   '?wallet-id]])
          (concat-when currency-id
            [['?account-id o.accounts/currency '?currency-id]])
          (concat-when rate-id
            [['?account-id o.accounts/source   '?rate-rate-source-id]
             ['?rate-id    o.rates/source      '?rate-rate-source-id]])
          (concat-when user-id
            [['?account-id o.accounts/user      '?user-id]])))})

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

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.accounts/id)]
  (log/debug :accounts/find-by-user {:user-id user-id})
  (c.xtdb/query-values
   '{:find  [?account-id]
     :in    [[?user-id]]
     :where [[?account-id ::m.accounts/user ?user-id]]}
   [user-id]))

(>defn create!
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
