(ns dinsro.ui.rates
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(defattr currency-link ::m.rates/currency :ref
  {ao/cardinality      :one
   ao/identities       #{::m.rates/id}
   ao/target           ::m.currencies/id
   ::report/column-EQL {::m.rates/currency (comp/get-query u.links/CurrencyLink)}})

(defattr source-link ::m.rates/source :ref
  {ao/cardinality      :one
   ao/identities       #{::m.rates/id}
   ao/target           ::m.rate-sources/id
   ::report/column-EQL {::m.rates/source (comp/get-query u.links/RateSourceLink)}})

(form/defsc-form RateForm [_this _props]
  {fo/id           m.rates/id
   fo/attributes   []
   fo/route-prefix "rate"
   fo/title        "Rate"})

(report/defsc-report RatesReport
  [_this _props]
  {ro/field-formatters
   {::m.rates/currency (fn [_this props] (u.links/ui-currency-link props))
    ::m.rates/source (fn [_this props] (u.links/ui-rate-source-link props))}
   ro/columns          [m.rates/rate
                        source-link
                        m.rates/date]
   ro/route            "rates"
   ro/row-actions      []
   ro/row-pk           m.rates/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.rates/all-rates
   ro/title            "Rates Report"})
