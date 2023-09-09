(ns dinsro.mocks.ui.forms.admin.transactions
  (:require
   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.options.debits :as o.debits]
   [dinsro.options.transactions :as o.transactions]
   [dinsro.specs :as ds]))

;; project://src/main/dinsro/ui/forms/admin/transactions.cljc
;; project://src/test/dinsro/ui/forms/admin/transactions_test.cljs

(defn NewDebit-state
  []
  {o.debits/value (ds/gen-key o.debits/value)
   ::fs/config
   {::fs/complete?      #{o.debits/account
                          o.debits/value}
    ::fs/fields         #{o.debits/account
                          o.debits/value}
    ::fs/id             [:dinsro.model.debits/id  (ds/gen-key ::m.debits/id)]
    ::fs/pristine-state {}
    ::fs/subforms       {}}})

(defn get-state
  []
  {o.transactions/id          (ds/gen-key o.transactions/id)
   o.transactions/description (ds/gen-key o.transactions/description)
   o.transactions/date        (ds/gen-key o.transactions/date)
   ::fs/config
   {::fs/complete?      #{o.transactions/description
                          o.transactions/date
                          ::j.transactions/debits}
    ::fs/fields         #{o.transactions/description
                          o.transactions/date
                          ::j.transactions/debits}
    ::fs/id             [:dinsro.model.transactions/id  (ds/gen-key ::m.transactions/id)]
    ::fs/pristine-state {}
    ::fs/subforms       {}}})
