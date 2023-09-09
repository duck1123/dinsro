(ns dinsro.mocks.ui.forms.transactions
  (:require
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.options.transactions :as o.transactions]
   [dinsro.specs :as ds]))

;; [[../../../ui/forms/transactions.cljc]]
;; [[../../../../../test/dinsro/ui/forms/transactions_test.cljs]]

(defn NewDebit-data
  [_a]
  {o.transactions/description (ds/gen-key o.transactions/description)
   o.transactions/date        (ds/gen-key o.transactions/date)
   o.transactions/id          (ds/gen-key o.transactions/id)
   ::j.transactions/debits    (ds/gen-key j.transactions/debits)})

(defn NewTransaction-data
  [_a]
  {o.transactions/description (ds/gen-key o.transactions/description)
   o.transactions/date        (ds/gen-key o.transactions/date)
   o.transactions/id          (ds/gen-key o.transactions/id)
   ::j.transactions/debits    (ds/gen-key j.transactions/debits)})

(defn EditForm-data
  [_a]
  {o.transactions/description (ds/gen-key o.transactions/description)
   o.transactions/date        (ds/gen-key o.transactions/date)
   o.transactions/id          (ds/gen-key o.transactions/id)
   ::j.transactions/debits    (ds/gen-key j.transactions/debits)})
