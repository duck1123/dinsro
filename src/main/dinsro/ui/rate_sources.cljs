(ns dinsro.ui.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.rates :as u.rates]))

(form/defsc-form RateSubform
  [_this _props]
  {fo/id           m.rates/id
   fo/route-prefix "rate-source-rate"
   fo/attributes   [m.rates/rate
                    m.rates/date]})

(def run-button
  {:type   :button
   :local? true
   :label  "Run"
   :action
   (fn [this _key]
     (let [{::m.rate-sources/keys [id]} (comp/props this)]
       (comp/transact! this [(mu.rate-sources/run-query! {::m.rate-sources/id id})])))})

(form/defsc-form RateSourceForm
  [_this _props]
  {fo/id             m.rate-sources/id
   fo/action-buttons (concat [::run] form/standard-action-buttons)
   fo/controls       (merge form/standard-controls {::run run-button})
   fo/subforms       {::m.rate-sources/currency     {fo/ui u.links/CurrencyLinkForm}
                      ::m.rate-sources/current-rate {fo/ui u.rates/RateSubForm}
                      ::m.rate-sources/rates        {fo/ui u.rates/RateSubForm}}
   fo/attributes     [m.rate-sources/name
                      m.rate-sources/url
                      m.rate-sources/active?
                      m.rate-sources/path
                      j.rate-sources/rates]
   fo/cancel-route   ["rate-sources"]
   fo/field-styles   {::m.rate-sources/rates :rate-chart}
   fo/route-prefix   "rate-source"
   fo/title          "Rate Source"})

(report/defsc-report RateSourcesReport
  [_this _props]
  {ro/columns          [m.rate-sources/name
                        m.rate-sources/url
                        m.rate-sources/active?]
   ro/controls         {::new-rate-source {:label  "New Source"
                                           :type   :button
                                           :action (fn [this] (form/create! this RateSourceForm))}}
   ro/control-layout   {:action-buttons [::new-rate-source]}
   ro/form-links       {::m.rate-sources/name RateSourceForm}
   ro/field-formatters
   {::m.rate-sources/currency (fn [_this props] (u.links/ui-currency-link props))}
   ro/route            "rate-sources"
   ro/row-actions      []
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.rate-sources/index
   ro/title            "Rate Sources Report"})

(report/defsc-report AdminIndexRateSourcesReport
  [_this _props]
  {ro/columns          [m.rate-sources/name]
   ro/source-attribute ::m.rate-sources/index
   ro/title            "Rate Sources"
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true})
