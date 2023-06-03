(ns dinsro.ui.transactions
  (:require
   ;; [clojure.instant :as cinst]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.addons.pagination.ui-pagination :as sui-pagination]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button-group :refer [ui-button-group]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.header.ui-header :refer [ui-header]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-list :refer [ui-list-list]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.formatters.date-time :as f.date-time]
   [dinsro.joins.debits :as j.debits]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.navlinks :as m.navlinks :refer [defroute]]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.currencies :as o.currencies]
   [dinsro.options.debits :as o.debits]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.transactions :as o.transactions]
   [dinsro.specs :as ds]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.transactions :as u.f.transactions]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.transactions.debits :as u.t.debits]
   [lambdaisland.glogc :as log]
   [tick.core :as t]))

;; [[../joins/transactions.cljc]]
;; [[../model/transactions.cljc]]
;; [[../ui/admin/transactions.cljs]]
;; [[../../../test/dinsro/ui/transactions_test.cljs]]

(def index-page-id :transactions)
(def model-key o.transactions/id)
(def parent-router-id :root)
(def required-role :user)
(def show-page-id :transactions-show)

(def show-controls false)
(def debug-debits? false)
(def use-moment true)

(defsc CurrencyInfo
  [_this {::m.currencies/keys [name]}]
  {:ident         ::m.currencies/id
   :initial-state (fn [_props]
                    {o.currencies/id   nil
                     o.currencies/name ""})
   :query         (fn []
                    [o.currencies/id
                     o.currencies/name])}
  (dom/div {} (str name)))

(defsc AccountInfo
  [_this {name                              o.accounts/name
          {currency-name o.currencies/name} o.accounts/currency}]
  {:ident         ::m.accounts/id
   :initial-state (fn [_props]
                    {o.accounts/currency (comp/get-initial-state CurrencyInfo {})
                     o.accounts/id       nil
                     o.accounts/name     ""})
   :query         (fn []
                    [{o.accounts/currency (comp/get-query CurrencyInfo)}
                     o.accounts/id
                     o.accounts/name])}
  (dom/div {}
    (dom/div {} (str name))
    (dom/div {} (str currency-name))))

(defsc DebitLine-List
  [_this {::j.debits/keys [event-value]
          account         o.debits/account
          value           o.debits/value
          :as             props}]
  {:ident         ::m.debits/id
   :initial-state (fn [_props]
                    {o.debits/id                   nil
                     o.debits/value                0
                     o.debits/account              (comp/get-initial-state AccountInfo {})
                     ::j.debits/event-value        0
                     ::j.debits/current-rate       (comp/get-initial-state u.links/RateValueLinkForm {})
                     ::j.debits/current-rate-value 0})
   :query         (fn []
                    [o.debits/id
                     o.debits/value
                     {o.debits/account (comp/get-query AccountInfo)}
                     ::j.debits/event-value
                     {::j.debits/current-rate (comp/get-query u.links/RateValueLinkForm)}
                     ::j.debits/current-rate-value])}
  (let [currency (o.accounts/currency account)]
    (ui-list-item {}
      (ui-grid {:celled true :padded false}
        (ui-grid-row {}
          (ui-grid-column {:computer 10 :tablet 10 :mobile 16}
            (u.links/ui-account-link account))
          (ui-grid-column {:computer 6 :tablet 6 :mobile 16}
            (ui-grid {:padded false}
              (ui-grid-row {}
                (ui-grid-column {:computer 12 :tablet 8 :mobile 8 :textAlign "right"}
                  (str value))
                (ui-grid-column {:computer 4 :tablet 8 :mobile 8}
                  (when currency
                    (u.links/ui-currency-link currency)))
                (when (not= value event-value)
                  (comp/fragment
                   (ui-grid-column {:computer 12 :tablet 8 :mobile 8 :textAlign "right"}
                     (str event-value))
                   (ui-grid-column {:computer 4 :tablet 8 :mobile 8} "sats")))))))
        (when debug-debits?
          (ui-grid-row {}
            (ui-grid-column {:width 16}
              (dom/div {} "Debug section")
              (u.debug/log-props props))))))))

(def ui-debit-list-line (comp/factory DebitLine-List {:keyfn o.debits/id}))

(defsc BodyItem
  [_this {::m.transactions/keys [date]
          ::j.transactions/keys [positive-debits negative-debits]
          :as                   props}]
  {:ident         ::m.transactions/id
   :initial-state (fn [_props]
                    {o.transactions/id                nil
                     o.transactions/date              nil
                     o.transactions/description       ""
                     ::j.transactions/positive-debits []
                     ::j.transactions/negative-debits []})}
  :query         (fn []
                   [o.transactions/id
                    o.transactions/date
                    o.transactions/description
                    {::j.transactions/positive-debits (comp/get-query DebitLine-List)}
                    {::j.transactions/negative-debits (comp/get-query DebitLine-List)}])
  (log/info :BodyItem/starting {:props props})
  (dom/div :.ui.item
    (ui-segment {}
      (ui-grid {:padded false}
        (ui-grid-row {}
          (ui-grid-column {:width 8}
            (u.links/ui-transaction-link props))
          (ui-grid-column {:textAlign "right" :width 8}
            (if use-moment
              (when date
                (let [iso (f.date-time/->iso (ds/->inst date))]
                  (u.controls/relative-date iso)))
              (str date)))))
      (ui-list-list {}
        (map ui-debit-list-line positive-debits)
        (map ui-debit-list-line negative-debits)))))

(def ui-body-item (comp/factory BodyItem {:keyfn o.transactions/id}))

(def new-button
  {:label  "New Transaction"
   :type   :button
   :action (fn [this] (form/create! this u.f.transactions/NewTransaction))})

(report/defsc-report Report
  [this props]
  {ro/BodyItem            BodyItem
   ro/column-formatters   {o.transactions/description #(u.links/ui-transaction-link %3)}
   ro/columns             [m.transactions/description
                           m.transactions/date
                           j.transactions/debit-count]
   ro/control-layout      {:action-buttons [::new ::refresh]}
   ro/controls            {::new     new-button
                           ::refresh u.links/refresh-control}
   ro/initial-sort-params {:sort-by          o.transactions/date
                           :sortable-columns #{o.transactions/date}
                           :ascending?       false}
   ro/machine             spr/machine
   ro/page-size           10
   ro/paginate?           true
   ro/row-pk              m.transactions/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.transactions/index
   ro/title               "Transaction Report"}
  #_(log/trace :Report/starting {:props props})
  (let [{:ui/keys [current-rows]} props]
    (ui-container {#_#_:centered true}
      (ui-segment {}
        (ui-grid {}
          (ui-grid-row {#_#_:centered true}
            (ui-grid-column {:width 12}
              (ui-header {}
                (dom/span {} "Transactions")))
            (ui-grid-column {:width 4 :floated "right"}
              (ui-button-group {:compact true :floated "right"}
                (ui-button {:onClick (fn [_] (form/create! this u.f.transactions/NewTransaction))} "New")
                (ui-button {:icon "refresh" :onClick (fn [_] (control/run! this))})))))
        (when show-controls
          ((report/control-renderer this) this))
        (let [page-count (report/page-count this)]
          (sui-pagination/ui-pagination
           {:activePage   (report/current-page this)
            :onPageChange (fn [_ data]
                            (report/goto-page! this (comp/isoget data "activePage")))
            :totalPages   page-count
            :size         "tiny"}))
        (ui-container {#_#_:centered true}
          (map ui-body-item current-rows))))))

(def ui-report
  "Index transactions report"
  (comp/factory Report))

(report/defsc-report RecentReport
  [_this props]
  {ro/BodyItem            BodyItem
   ro/columns             [m.transactions/description
                           m.transactions/date
                           j.transactions/debit-count]
   ro/control-layout      {:action-buttons [::new-transaction ::refresh]}
   ro/controls            {::new-transaction new-button
                           ::refresh         u.links/refresh-control}
   ro/field-formatters    {o.transactions/description #(u.links/ui-transaction-link %3)}
   ro/initial-sort-params {:sort-by          o.transactions/date
                           :sortable-columns #{o.transactions/date}
                           :ascending?       false}
   ro/machine             spr/machine
   ro/page-size           10
   ro/paginate?           true
   ro/row-pk              m.transactions/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.transactions/index
   ro/title               "Recent Transactions"}
  (let [{:ui/keys [current-rows]} props]
    (ui-container {}
      (map ui-body-item  current-rows))))

(defsc Show
  [this {::m.transactions/keys [description date id]
         :ui/keys              [debits]
         :as                   props}]
  {:ident         ::m.transactions/id
   :initial-state (fn [props]
                    {o.transactions/description ""
                     o.transactions/id          (model-key props)
                     o.transactions/date        ""
                     :ui/debits                 (comp/get-initial-state u.t.debits/SubSection {})})
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/debits [u.t.debits/SubSection {}]})
   :query         (fn []
                    [o.transactions/description
                     o.transactions/id
                     o.transactions/date
                     {:ui/debits (comp/get-query u.t.debits/SubSection)}])}
  (log/debug :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} (str description))
        (dom/p {}
          (dom/span {} "Date: ")
          (dom/span {} (u.controls/relative-date date)))
        (u.buttons/form-edit-button this model-key "Edit" u.f.transactions/NewTransaction))
      (if debits
        (ui-segment {}
          (u.t.debits/ui-sub-section debits))
        (u.debug/load-error props "show transaction debits")))
    (u.debug/load-error props "show transaction record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [_] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["transactions"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/finest :IndexPage/starting {:props props})
  (if report
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["transaction" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Transactions"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Transaction"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
