(ns dinsro.ui.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.joins :as m.joins]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(defattr currency-link ::m.accounts/currency :ref
  {ao/cardinality                         :one
   ao/identities                          #{::m.accounts/id}
   ao/target                              ::m.currencies/id
   ::report/column-EQL {::m.accounts/currency (comp/get-query u.links/CurrencyLink)}})

(defattr user-link ::m.accounts/user :ref
  {ao/cardinality                         :one
   ao/identities                          #{::m.accounts/id}
   ao/target                              ::m.users/id
   ::report/column-EQL {::m.accounts/user (comp/get-query u.links/UserLink)}})

(def override-form true)

(form/defsc-form AccountForm
  [this {::m.accounts/keys [currency name initial-value user]
         :as               props}]
  {fo/id             m.accounts/id
   fo/subforms       {::m.accounts/user     {fo/ui u.links/UserLinkForm}
                      ::m.accounts/currency {fo/ui u.links/CurrencyLinkForm}
                      ::m.accounts/transactions {fo/ui u.links/TransactionLinkForm}}
   fo/field-styles {::m.accounts/transactions :link}
   fo/attributes     [m.accounts/name
                      m.accounts/currency
                      m.accounts/user
                      m.accounts/initial-value
                      m.joins/account-transactions]
   fo/cancel-route ["accounts"]
   fo/route-prefix   "account"
   fo/title          "Edit Account"}
  (if override-form
    (form/render-layout this props)
    (dom/div :.ui.container
      (dom/p {} (str "Account: " name))
      (dom/p {} (str "Initial Value: " initial-value))
      (dom/p {} (str "Currency: " currency))
      (dom/p {} (str "User: " user)))))

(report/defsc-report AccountsReport
  [_this _props]
  {ro/form-links       {::m.accounts/name AccountForm}
   ro/field-formatters
   {::m.accounts/currency (fn [_this props] (u.links/ui-currency-link props))
    ::m.accounts/user     (fn [_this props] (u.links/ui-user-link props))}
   ro/columns          [m.accounts/name
                        currency-link
                        user-link
                        m.accounts/initial-value]
   ro/route            "accounts"
   ro/row-actions      []
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.accounts/all-accounts
   ro/title            "Accounts"})
