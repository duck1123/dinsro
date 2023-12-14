(ns dinsro.mocks.ui.reports.accounts
  (:require
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.core.wallets :as o.c.wallets]
   [dinsro.options.currencies :as o.currencies]
   [dinsro.options.debits :as o.debits]
   [dinsro.specs :as ds]))

;; [[../../../ui/reports/accounts.cljc]]
;; [[../../../../../test/dinsro/ui/reports/accounts_test.cljs]]

(def row-count 3)

(defn BodyItem-list-data
  [_opts]
  {o.accounts/currency      {o.currencies/id   (ds/gen-key o.currencies/id)
                             o.currencies/name (ds/gen-key o.currencies/name)}
   o.accounts/initial-value (ds/gen-key o.accounts/initial-value)
   o.accounts/name          (ds/gen-key o.accounts/name)
   o.accounts/wallet        {o.c.wallets/id   (ds/gen-key o.c.wallets/id)
                             o.c.wallets/name (ds/gen-key o.c.wallets/name)}
   ::j.accounts/debit-count (ds/gen-key ::j.accounts/debit-count)
   ::j.accounts/debits      []})

(defn BodyItem-table-data
  [_opts]
  {o.accounts/currency      {o.currencies/id   (ds/gen-key o.currencies/id)
                             o.currencies/name (ds/gen-key o.currencies/name)}
   o.accounts/initial-value (ds/gen-key o.accounts/initial-value)
   o.accounts/name          (ds/gen-key o.accounts/name)
   o.accounts/wallet        {o.c.wallets/id   (ds/gen-key o.c.wallets/id)
                             o.c.wallets/name (ds/gen-key o.c.wallets/name)}
   ::j.accounts/debit-count (ds/gen-key ::j.accounts/debit-count)
   ::j.accounts/debits      []})

(defn DebitLine-data
  [_opts]
  {o.debits/id    (ds/gen-key o.debits/id)
   o.debits/value (ds/gen-key o.debits/value)})

(defn SubPage-row
  [_opts]
  {:foo "bar"})

(defn Report-data
  [_opts]
  {:foo "bar"
   :ui/controls     []
   :ui/current-rows (map (fn [_] (BodyItem-list-data {})) (range row-count))
   :ui/busy?        false
   :ui/parameters   {}
   :ui/page-count   1
   :ui/current-page 1
   :ui/cache        {}})
