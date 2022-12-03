(ns dinsro.ui.rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.rates :as m.rates]
   [dinsro.ui.links :as u.links]))

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

(report/defsc-report RatesReport
  [_this _props]
  {ro/columns          [m.rates/rate
                        m.rates/source
                        m.rates/date]
   ro/field-formatters {::m.rates/currency #(u.links/ui-currency-link %2)
                        ::m.rates/source   #(u.links/ui-rate-source-link %2)}
   ro/route            "rates"
   ro/row-actions      []
   ro/row-pk           m.rates/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.rates/index
   ro/title            "Rates Report"})
