(ns dinsro.ui.forms.currencies
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.currencies :as m.currencies]))

;; [[../actions/currencies.clj]]
;; [[../joins/currencies.cljc]]
;; [[../model/currencies.cljc]]

(def index-page-id :currencies)
(def model-key ::m.currencies/id)
(def parent-router-id :root)
(def required-role :user)
(def show-page-id :currencies-show)

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/id           m.currencies/id
   fo/route-prefix "new-currency"
   fo/title        "New Currency"})

(form/defsc-form NewAdminCurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/route-prefix "new-admin-currency"
   fo/title        "New Currency"})

(form/defsc-form CurrencyForm [this props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code
                    j.currencies/accounts
                    j.currencies/sources
                    j.currencies/current-rate]
   fo/field-styles {::m.currencies/accounts     :link-list
                    ::m.currencies/sources      :link-list
                    ::m.currencies/transactions :link-list}
   fo/cancel-route ["currencies"]
   fo/route-prefix "currency"
   fo/subforms     {::m.currencies/accounts     {fo/ui u.links/AccountLinkForm}
                    ::m.currencies/sources      {fo/ui u.links/RateSourceLinkForm}
                    ::m.currencies/transactions {fo/ui u.links/TransactionLinkForm}}
   fo/title        "Currency"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/h1 "Currency")
      (form/render-layout this props)
      (u.accounts/ui-accounts-sub-report {}))))

(form/defsc-form AdminCurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/route-prefix "new-admin-currency"
   fo/title        "New Currency"})
