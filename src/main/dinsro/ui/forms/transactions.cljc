(ns dinsro.ui.forms.transactions
  (:require
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.options.debits :as o.debits]
   [dinsro.options.transactions :as o.transactions]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.pickers :as u.pickers]))

;; [[../../../../test/dinsro/ui/forms/transactions_test.cljs]]

(def index-page-id :transactions)
(def model-key o.transactions/id)
(def parent-router-id :root)
(def required-role :user)
(def show-page-id :transactions-show)

(def log-props? true)
(def show-controls false)
(def debug-debits? false)
(def override-form? false)
(def use-table true)
(def use-moment true)

(form/defsc-form NewDebit
  [_this _props]
  {fo/attributes    [m.debits/value
                     m.debits/account]
   fo/field-options {o.debits/account u.pickers/account-picker}
   fo/field-styles  {o.debits/account :pick-one}
   fo/title         "Debit"
   fo/route-prefix  "new-debit"
   fo/id            m.debits/id})

(form/defsc-form NewTransaction
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
        (dom/div {} "foo")))
    (when log-props?
      (u.debug/log-props props))))

(form/defsc-form EditForm [_this _props]
  {fo/attributes    [m.transactions/description]
   fo/cancel-route  ["transactions"]
   fo/id            m.transactions/id
   fo/route-prefix  "edit-transaction-form"
   fo/title         "Transaction"})
