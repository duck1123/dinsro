(ns dinsro.ui.admin.ln.invoices
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.invoices :as j.ln.invoices]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.ln.invoices :as o.ln.invoices]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.forms.admin.ln.invoices :as u.f.a.ln.invoices]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/ln/invoices.cljc]]
;; [[../../../model/ln/invoices.cljc]]

(def index-page-id :admin-ln-invoices)
(def model-key o.ln.invoices/id)
(def parent-router-id :admin-ln)
(def required-role :admin)
(def show-page-id :admin-ln-invoices-show)

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.a.ln.invoices/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.invoices/id
                        m.ln.invoices/memo
                        m.ln.invoices/settled?
                        m.ln.invoices/creation-date
                        m.ln.invoices/node]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/field-formatters {o.ln.invoices/node #(u.links/ui-node-link %2)
                        o.ln.invoices/id #(u.links/ui-invoice-link %3)}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route            "invoices"
   ro/row-pk           m.ln.invoices/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.invoices/index
   ro/title            "Lightning Invoices Report"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.ln.invoices/id
   :initial-state (fn [props]
                    {o.ln.invoices/id (model-key props)})
   :query         (fn []
                    [o.ln.invoices/id])
   :route-segment ["invoices" :id]}
  (dom/div {}))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["invoices"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Invoices"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/required-role required-role
   o.navlinks/router        parent-router-id})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Invoice"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/required-role required-role
   o.navlinks/router        parent-router-id})
