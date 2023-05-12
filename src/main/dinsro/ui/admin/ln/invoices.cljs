(ns dinsro.ui.admin.ln.invoices
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.invoices :as j.ln.invoices]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.mutations.ln.invoices :as mu.ln.invoices]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [props (comp/props this)]
               (log/info :submit-button/starting {:props props})
               (comp/transact! this [(mu.ln.invoices/submit! props)])))})

(form/defsc-form NewForm [this props]
  {fo/action-buttons [::submit]
   fo/attributes     [m.ln.invoices/memo
                      m.ln.invoices/value]
   fo/controls       {::submit submit-button}
   fo/id             m.ln.invoices/id
   fo/route-prefix   "new-invoice"
   fo/title          "New Invoice"}
  (dom/div {}
    (form/render-layout this props)))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.invoices/id
                        m.ln.invoices/memo
                        m.ln.invoices/settled?
                        m.ln.invoices/creation-date
                        m.ln.invoices/node]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/field-formatters {::m.ln.invoices/node #(u.links/ui-node-link %2)
                        ::m.ln.invoices/id #(u.links/ui-invoice-link %3)}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route            "invoices"
   ro/row-pk           m.ln.invoices/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.invoices/index
   ro/title            "Lightning Invoices Report"})

(defsc Show
  [_this _props]
  {:ident         ::m.ln.invoices/id
   :initial-state {::m.ln.invoices/id nil}
   :query         [::m.ln.invoices/id]
   :route-segment ["invoices" :id]}
  (dom/div {}))
