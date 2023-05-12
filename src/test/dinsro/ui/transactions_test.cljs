(ns dinsro.ui.transactions-test
  (:require
   [dinsro.client :as client]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs :as ds]
   [dinsro.ui.transactions :as u.transactions]
   [dinsro.ui.transactions.debits-test :as t.u.t.debits]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

(defn make-transaction
  []
  {::m.transactions/description (ds/gen-key ::m.transactions/description)
   ::m.transactions/date        (ds/gen-key ::m.transactions/date)
   ::m.transactions/id          (ds/gen-key ::m.transactions/id)
   ::j.transactions/debit-count (ds/gen-key ::j.transactions/debit-count)
   :ui/debits                   (t.u.t.debits/make-sub-page)})

(defn make-currency
  []
  {::m.currencies/id   (ds/gen-key ::m.currencies/id)
   ::m.currencies/name (ds/gen-key ::m.currencies/name)})

(defn make-account
  []
  {::m.accounts/id       (ds/gen-key ::m.accounts/id)
   ::m.accounts/name     (ds/gen-key ::m.accounts/name)
   ::m.accounts/currency (make-currency)})

(defn make-debit-list-line
  []
  {::m.debits/id      (ds/gen-key ::m.debits/id)
   ::m.debits/value   (ds/gen-key ::m.debits/value)
   ::m.debits/account (make-account)})

(defn make-body-item
  []
  {::m.transactions/description     (ds/gen-key ::m.transactions/description)
   ::m.transactions/date            (ds/gen-key ::m.transactions/date)
   ::m.transactions/id              (ds/gen-key ::m.transactions/id)
   ::j.transactions/negative-debits (map (fn [_] (make-debit-list-line)) (range 3))
   ::j.transactions/positive-debits (map (fn [_] (make-debit-list-line)) (range 3))})

(ws/defcard DebitListLine
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.transactions/DebitLine-List
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state (fn [] (make-debit-list-line))}))

(ws/defcard BodyItem
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.transactions/BodyItem
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state (fn [] (make-body-item))}))

(ws/defcard Show
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.transactions/Show
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state (fn [] (make-transaction))}))
