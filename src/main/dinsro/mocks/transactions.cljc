(ns dinsro.mocks.transactions
  (:require
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.mocks.debits :as mo.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs :as ds]))

(defn make-transaction
  []
  {::m.transactions/description (ds/gen-key ::m.transactions/description)
   ::m.transactions/date        (ds/gen-key ::m.transactions/date)
   ::m.transactions/id          (ds/gen-key ::m.transactions/id)
   ::j.transactions/debit-count (ds/gen-key ::j.transactions/debit-count)
   :ui/debits                   (mo.debits/make-sub-page)})

(defn make-body-item
  []
  {::m.transactions/description     (ds/gen-key ::m.transactions/description)
   ::m.transactions/date            (ds/gen-key ::m.transactions/date)
   ::m.transactions/id              (ds/gen-key ::m.transactions/id)
   ::j.transactions/negative-debits (map (fn [_] (mo.debits/make-debit-list-line)) (range 3))
   ::j.transactions/positive-debits (map (fn [_] (mo.debits/make-debit-list-line)) (range 3))})
