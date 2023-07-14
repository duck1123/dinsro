(ns dinsro.ui.admin.ln.invoices
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.invoices :as j.ln.invoices]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.ln.invoices :as mu.ln.invoices]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/ln/invoices.cljc]]
;; [[../../../model/ln/invoices.cljc]]

(def index-page-key :admin-ln-invoices)
(def model-key ::m.ln.invoices/id)

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [props (comp/props this)]
               (log/info :submit-button/starting {:props props})
               (comp/transact! this [`(mu.ln.invoices/submit! ~props)])))})

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

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.ln.invoices/id
   :initial-state {::m.ln.invoices/id nil}
   :query         [::m.ln.invoices/id]
   :route-segment ["invoices" :id]}
  (dom/div {}))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["invoices"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(m.navlinks/defroute :admin-ln-invoices
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Invoices"
   ::m.navlinks/model-key     ::m.ln.invoices/id
   ::m.navlinks/parent-key    :admin-ln
   ::m.navlinks/required-role :admin
   ::m.navlinks/router        :admin-ln})

(m.navlinks/defroute :admin-ln-invoices-show
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/label         "Show Invoice"
   ::m.navlinks/model-key     ::m.ln.invoices/id
   ::m.navlinks/parent-key    :admin-ln-invoices
   ::m.navlinks/router        :admin-ln
   ::m.navlinks/required-role :admin})
