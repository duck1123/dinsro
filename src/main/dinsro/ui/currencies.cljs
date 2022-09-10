(ns dinsro.ui.currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.ui.currency-accounts :as u.currency-accounts]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.rates :as u.rates]))

(form/defsc-form NewCurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code]
   ;; fo/cancel-route ["admin"]
   fo/route-prefix "new-currency"
   fo/title        "New Currency"})

(form/defsc-form CurrencyForm [_this _props]
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
                    ::m.currencies/current-rate {fo/ui u.rates/RateSubForm}
                    ::m.currencies/sources      {fo/ui u.links/RateSourceLinkForm}
                    ::m.currencies/transactions {fo/ui u.links/TransactionLinkForm}}
   fo/title        "Currency"})

(form/defsc-form AdminCurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/cancel-route ["admin"]
   fo/route-prefix "admin/currency"
   fo/title        "Currency"})

(def new-button
  {:label  "New"
   :type   :button
   :action #(form/create! % NewCurrencyForm)})

(report/defsc-report CurrenciesReport
  [_this _props]
  {ro/column-formatters
   {::m.currencies/name
    (fn [this name {::m.currencies/keys [id]}]
      (dom/a {:onClick #(form/edit! this CurrencyForm id)} name))}
   ro/columns          [m.currencies/name
                        m.currencies/code]
   ro/controls         {::new new-button}
   ro/route            "currencies"
   ro/row-actions      []
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.currencies/index
   ro/title            "Currencies Report"})

(report/defsc-report AdminIndexCurrenciesReport
  [_this _props]
  {ro/columns          [m.currencies/name m.currencies/code]
   ro/controls         {::new {:label  "New Currency"
                               :type   :button
                               :action #(form/create! % AdminCurrencyForm)}}
   ro/source-attribute ::m.currencies/index
   ro/title            "Currencies"
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true})

(defsc ShowCurrency
  [_this {::m.currencies/keys [name]
          :ui/keys       [accounts]}]
  {:route-segment ["currency" :id]
   :query         [::m.currencies/name
                   ::m.currencies/code
                   ::m.currencies/id
                   {:ui/accounts (comp/get-query u.currency-accounts/SubPage)}]
   :initial-state {::m.currencies/name ""
                   ::m.currencies/code ""
                   ::m.currencies/id   nil
                   :ui/accounts        {}}
   :ident         ::m.currencies/id
   :pre-merge     (u.links/page-merger
                   ::m.currencies/id
                   {:ui/accounts u.currency-accounts/SubPage})
   :will-enter    (partial u.links/page-loader ::m.currencies/id ::ShowCurrency)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show Currency " (str name)))
   (u.currency-accounts/ui-sub-page accounts)))
