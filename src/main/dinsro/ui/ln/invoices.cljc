(ns dinsro.ui.ln.invoices
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.ln.invoices :as j.ln.invoices]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.forms.ln.invoices :as u.f.ln.invoices]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/ln/invoices.cljc]]
;; [[../../model/ln/invoices.cljc]]

(def index-page-id :ln-invoices)
(def model-key ::m.ln.invoices/id)
(def parent-router-id :ln)
(def required-role :user)
(def show-page-id :ln-invoices-show)

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.ln.invoices/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.invoices/node #(u.links/ui-node-link %2)
                         ::m.ln.invoices/id   #(u.links/ui-invoice-link %3)}
   ro/columns           [m.ln.invoices/id
                         m.ln.invoices/memo
                         m.ln.invoices/settled?
                         m.ln.invoices/creation-date
                         m.ln.invoices/node]
   ro/control-layout    {:action-buttons [::new]}
   ro/controls          {::new new-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.ln.invoices/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.invoices/index
   ro/title             "Lightning Invoices Report"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.ln.invoices/id
   :initial-state {::m.ln.invoices/id nil}
   :query         [::m.ln.invoices/id]}
  (dom/div {}
    "TODO: Show invoice"))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["invoices"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.ln.invoices/keys [id]
          ::m.navlinks/keys             [target]
          :as                  props}]
  {:ident         (fn [] [::m.navlinks/id show-page-id])
   :initial-state {::m.ln.invoices/id nil
                   ::m.navlinks/id    show-page-id
                   ::m.navlinks/target         {}}
   :query         [::m.ln.invoices/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["invoice" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {:color "red" :inverted true}
      "Failed to load record")))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Index LN Nodes"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Node"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
