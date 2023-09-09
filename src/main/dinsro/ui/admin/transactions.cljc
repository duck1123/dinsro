(ns dinsro.ui.admin.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.mutations.transactions :as mu.transactions]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.admin.transactions :as u.f.a.transactions]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/transactions.cljc]]
;; [[../../model/transactions.cljc]]
;; [[../../ui/transactions.cljs]]

(def index-page-id :admin-transactions)
(def model-key ::m.transactions/id)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-id :admin-transactions-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.transactions/delete!))

(def new-button
  (u.buttons/form-create-button "New Transaction" u.f.a.transactions/AdminTransactionForm))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.transactions/description #(u.links/ui-admin-transaction-link %3)}
   ro/columns           [m.transactions/description
                         m.transactions/date
                         j.transactions/debit-count]
   ro/control-layout    {:action-buttons [::new-transaction ::refresh]}
   ro/controls          {::new-transaction new-button
                         ::refresh         u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.transactions/admin-index
   ro/title             "Admin Transaction Report"})

(def ui-report (comp/factory Report))

(defsc Show
  [this {::m.transactions/keys [description date id]
         ::j.transactions/keys [debit-count]
         :as                   props}]
  {:ident         ::m.transactions/id
   :initial-state {::m.transactions/description ""
                   ::m.transactions/id          nil
                   ::m.transactions/date        ""
                   ::j.transactions/debit-count 0}
   :query         [::m.transactions/description
                   ::m.transactions/id
                   ::m.transactions/date
                   ::j.transactions/debit-count]}
  (log/debug :Show/starting {:props props})
  (dom/div {}
    (if id
      (ui-segment {}
        (dom/h1 {} (str description))
        (dom/div {} (str "Debit Count: " debit-count))
        (dom/p {}
          (dom/span {} "Date: ")
          (dom/span {} (u.controls/relative-date date)))
        (u.buttons/form-edit-button this model-key "Edit" u.f.a.transactions/AdminTransactionForm))
      (u.debug/load-error props "asmin show transactions"))))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["transactions"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query        (fn []
                   [o.navlinks/id
                    {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["transaction" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Transactions"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Admin show page for transaction"
   o.navlinks/label         "Show Transaction"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
