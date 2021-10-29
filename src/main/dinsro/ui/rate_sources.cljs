(ns dinsro.ui.rate-sources
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
   [dinsro.model.joins :as m.joins]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [edn-query-language.core :as eql]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(def default-name "sally")
(def default-url "https://example.com/")

(form/defsc-form RateSubform
  [_this _props]
  {fo/id m.rates/id
   fo/attributes [m.rates/rate
                  m.rates/date]})

(form/defsc-form RateSourceForm
  [_this _props]
  {fo/id           m.rate-sources/id
   fo/subforms     {::m.rate-sources/rates {fo/ui RateSubform}}
   fo/attributes   [m.rate-sources/name
                    m.rate-sources/url
                    m.joins/rate-source-rates]
   fo/route-prefix "rate-source"
   fo/title        "Rate Source"})

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

(defattr source-currency-link ::m.rate-sources/currency :ref
  {ao/cardinality      :one
   ao/identities       #{::m.rate-sources/id}
   ao/target           ::m.currencies/id
   ::report/column-EQL {::m.rate-sources/currency (comp/get-query u.links/CurrencyLink)}})

(report/defsc-report RateSourcesReport
  [_this _props]
  {ro/columns          [m.rate-sources/name
                        m.rate-sources/url
                        source-currency-link]
   ro/controls         {::new-rate-source {:label  "New Invoice"
                                           :type   :button
                                           :action (fn [this] (form/create! this RateSourceForm))}}
   ro/control-layout   {:action-buttons [::new-rate-source]}
   ro/field-formatters
   {::m.rate-sources/currency     (fn [_this props] (u.links/ui-currency-link props))}
   ro/route            "rate-sources"
   ro/row-actions      []
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.rate-sources/all-rate-sources
   ro/title            "Rate Sources Report"})

(report/defsc-report AdminIndexRateSourcesReport
  [_this _props]
  {ro/columns          [m.rate-sources/name]
   ro/source-attribute ::m.rate-sources/all-rate-sources
   ro/title            "Rate Sources"
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true})
