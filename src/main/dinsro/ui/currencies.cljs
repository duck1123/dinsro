(ns dinsro.ui.currencies
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
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(defattr currency-accounts ::m.currencies/accounts :ref
  {ao/cardinality      :one
   ao/identities       #{::m.currencies/id}
   ao/target           ::m.accounts/id
   ::report/column-EQL {::m.currencies/accounts (comp/get-query u.links/AccountLink)}})

(defattr currency-sources ::m.currencies/sources :ref
  {ao/cardinality      :one
   ao/identities       #{::m.currencies/id}
   ao/target           ::m.rate-sources/id
   ::report/column-EQL {::m.currencies/sources (comp/get-query u.links/RateSourceLink)}})

(defattr currency-transactions ::m.currencies/transactions :ref
  {ao/cardinality                         :one
   ao/identities                          #{::m.currencies/id}
   ao/target                              ::m.transactions/id
   ::report/column-EQL {::m.currencies/transactions (comp/get-query u.links/TransactionLink)}})

(form/defsc-form CurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code
                    currency-accounts
                    currency-sources
                    currency-transactions]
   fo/field-styles {::m.currencies/accounts :link-list}
   fo/cancel-route ["currencies"]
   fo/route-prefix "currency"
   fo/title        "Currency"})

(report/defsc-report CurrenciesReport
  [_this _props]
  {ro/column-formatters
   {::m.currencies/name
    (fn [this name {::m.currencies/keys [id]}]
      (dom/a {:onClick #(form/edit! this CurrencyForm id)} name))}
   ro/columns          [m.currencies/name
                        m.currencies/code]
   ro/route            "currencies"
   ro/row-actions      []
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.currencies/all-currencies
   ro/title            "Currencies Report"})

(report/defsc-report AdminIndexCurrenciesReport
  [_this _props]
  {ro/columns          [m.currencies/name]
   ro/source-attribute ::m.currencies/all-currencies
   ro/title            "Currencies"
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true})
