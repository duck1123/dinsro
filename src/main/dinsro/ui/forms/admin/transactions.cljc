(ns dinsro.ui.forms.admin.transactions
  (:require
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.pickers :as u.pickers]))

;; [[../../../../../test/dinsro/ui/forms/admin/transactions_test.cljs]]

(def debug-props? false)
(def override-form? true)

(form/defsc-form NewDebit
  [_this _props]
  {fo/attributes    [m.debits/value
                     m.debits/account]
   fo/field-options {::m.debits/account u.pickers/admin-account-picker}
   fo/field-styles  {::m.debits/account :pick-one}
   fo/title         "Debit"
   fo/route-prefix  "new-debit"
   fo/id            m.debits/id})

(form/defsc-form AdminTransactionForm
  [this props]
  {fo/attributes    [m.transactions/description
                     m.transactions/date
                     j.transactions/debits]
   fo/cancel-route  ["transactions"]
   fo/id            m.transactions/id
   fo/route-prefix  "new-transaction"
   fo/subforms      {::j.transactions/debits {fo/ui NewDebit}}
   fo/title         "Transaction"}
  (dom/div {}
    (if override-form?
      (form/render-layout this props)
      (dom/div {}
        (dom/p {} "Transaction form")))
    (when debug-props?
      (u.debug/ui-props-logger props))))
