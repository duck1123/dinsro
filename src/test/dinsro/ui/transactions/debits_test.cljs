(ns dinsro.ui.transactions.debits-test
  (:require
   [dinsro.client :as client]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs :as ds]
   [dinsro.ui.transactions.debits :as u.t.debits]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

(defn make-account
  []
  {::m.accounts/id   (ds/gen-key ::m.accounts/id)
   ::m.accounts/name (ds/gen-key ::m.accounts/name)})

(defn make-currency
  []
  {::m.currencies/id   (ds/gen-key ::m.currencies/id)
   ::m.currencies/name (ds/gen-key ::m.currencies/name)})

(defn Report-row
  []
  {::m.debits/id       (ds/gen-key ::m.debits/id)
   ::m.debits/value    (ds/gen-key ::m.debits/value)
   ::m.debits/account  (make-account)
   ::j.debits/currency (make-currency)})

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

(ws/defcard Report
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.t.debits/Report
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] (make-debit-report))}))

(ws/defcard SubPage
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.t.debits/SubSection
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] (make-sub-page))}))
