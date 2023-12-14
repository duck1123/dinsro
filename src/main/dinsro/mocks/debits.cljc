(ns dinsro.mocks.debits
  (:require
   [dinsro.joins.debits :as j.debits]
   [dinsro.mocks.accounts :as mo.accounts]
   [dinsro.mocks.currencies :as mo.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs :as ds]))

(defn CurrencyInfo-data
  []
  {})

(defn AccountInfo-data
  []
  {})

(defn make-debit-list-line
  []
  {::m.debits/id      (ds/gen-key ::m.debits/id)
   ::m.debits/value   (ds/gen-key ::m.debits/value)
   ::m.debits/account (mo.accounts/make-account)})

(defn Report-row
  []
  {::m.debits/id       (ds/gen-key ::m.debits/id)
   ::m.debits/value    (ds/gen-key ::m.debits/value)
   ::m.debits/account  (mo.accounts/make-account)
   ::j.debits/currency (mo.currencies/make-currency)})

(defn make-debit-report
  []
  {:ui/busy?        false
   :ui/cache        {}
   :ui/controls     []
   :ui/current-page 1
   :ui/current-rows (map (fn [_] (Report-row)) (range 3))
   :ui/loaded-data  []
   :ui/page-count   1
   :ui/parameters   {}})

(defn make-sub-page
  []
  {::m.transactions/id (ds/gen-key ::m.transactions/id)
   :ui/report          (make-debit-report)})
