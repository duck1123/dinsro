(ns dinsro.ui.rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.rates :as m.rates]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(defsc RefRow
  [_this {::m.rates/keys [rate] :as props}]
  {:ident ::m.rates/id
   :query [::m.rates/id
           ::m.rates/name
           ::m.rates/rate
           ::m.rates/currency]}
  (dom/tr {}
    (dom/td (str rate))
    (dom/td (u.links/ui-rate-link props))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.rates/id}))

(defn ref-table
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "Name")))
     (dom/tbody {}
       (for [tx value]
         (ui-ref-row tx))))))

(def render-ref-table (render-field-factory ref-table))

(def override-sub-form false)

(form/defsc-form RateSubForm [this {::m.rates/keys [date rate source] :as props}]
  {fo/id         m.rates/id
   fo/attributes [m.rates/rate
                  m.rates/source
                  m.rates/date]
   fo/subforms   {::m.rates/source {fo/ui u.links/RateSourceLinkForm}}
   fo/title      "Rate"}
  (if override-sub-form
    (form/render-layout this props)
    (dom/div {}
      (dom/div {} (str "Rate: " rate))
      (dom/div {}
        "Fetched at "
        (dom/span :.date (str date))
        " from "
        (u.links/ui-rate-source-link source)))))

(form/defsc-form RateForm [_this _props]
  {fo/id           m.rates/id
   fo/attributes   []
   fo/route-prefix "rate"
   fo/title        "Rate"})

(report/defsc-report RatesReport
  [_this _props]
  {ro/field-formatters
   {::m.rates/currency (fn [_this props] (u.links/ui-currency-link props))
    ::m.rates/source   (fn [_this props] (u.links/ui-rate-source-link props))}
   ro/columns          [m.rates/rate
                        m.rates/source
                        m.rates/date]
   ro/route            "rates"
   ro/row-actions      []
   ro/row-pk           m.rates/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.rates/index
   ro/title            "Rates Report"})
