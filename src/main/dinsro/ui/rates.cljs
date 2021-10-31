(ns dinsro.ui.rates
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [edn-query-language.core :as eql]
   [taoensso.timbre :as log]))

(defattr currency-link ::m.rates/currency :ref
  {ao/cardinality      :one
   ao/identities       #{::m.rates/id}
   ao/target           ::m.currencies/id
   ::report/column-EQL {::m.rates/currency (comp/get-query u.links/CurrencyLink)}})

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

(form/defsc-form RateForm [_this _props]
  {fo/id           m.rates/id
   fo/attributes   []
   fo/route-prefix "rate"
   fo/title        "Rate"})

(report/defsc-report RatesReport
  [_this _props]
  {ro/field-formatters
   {::m.rates/currency (fn [_this props] (u.links/ui-currency-link props))}
   ro/columns          [m.rates/id
                        m.rates/rate
                        currency-link
                        m.rates/date]
   ro/route            "rates"
   ro/row-actions      []
   ro/row-pk           m.rates/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.rates/all-rates
   ro/title            "Rates Report"})
