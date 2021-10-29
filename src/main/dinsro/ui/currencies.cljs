(ns dinsro.ui.currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.joins :as m.joins]
   [dinsro.translations :refer [tr]]
   [edn-query-language.core :as eql]
   [taoensso.timbre :as log]))

(defn- form-at-key [this k]
  (let [{:keys [children]} (eql/query->ast (comp/get-query this))]
    (some (fn [{:keys [key component]}] (when (and component (= key k)) component))
          children)))

(defn edit! [this form-key id]
  (let [Form (form-at-key this form-key)]
    (uism/trigger! this (comp/get-ident this)
                   :event/edit-detail
                   {:id       id
                    :form     Form
                    :join-key form-key})))

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
