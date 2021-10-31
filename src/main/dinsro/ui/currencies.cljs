(ns dinsro.ui.currencies
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.joins :as m.joins]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(form/defsc-form CurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code
                    m.joins/currency-accounts
                    m.joins/currency-sources
                    m.joins/currency-transactions]
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
