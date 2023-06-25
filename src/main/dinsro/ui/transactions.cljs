(ns dinsro.ui.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-cell :refer [ui-table-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-row :refer [ui-table-row]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button-group :refer [ui-button-group]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.joins.debits :as j.debits]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.controls :refer [ui-moment]]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.transactions.debits :as u.t.debits]
   [lambdaisland.glogc :as log]))

;; [[../joins/transactions.cljc]]
;; [[../model/transactions.cljc]]

(def index-page-key :transactions)
(def model-key ::m.transactions/id)
(def show-page-key :transactions-show)

(def show-debits-debug false)
(def use-table true)
(def use-moment true)

(defsc AccountQuery
  [_this _props]
  {:query [::m.accounts/id ::m.accounts/name]
   :ident ::m.accounts/id})

(form/defsc-form NewDebit
  [_this _props]
  {fo/attributes    [m.debits/value
                     m.debits/account]
   fo/field-options {::m.debits/account
                     {::picker-options/query-key       ::j.accounts/index
                      ::picker-options/query-component u.links/AccountLinkForm
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.accounts/keys [id name]}]
                           {:text  (str name)
                            :value [::m.accounts/id id]})
                         (sort-by ::m.accounts/name options)))}}
   fo/field-styles  {::m.debits/account :pick-one}
   fo/title         "Debit"
   fo/route-prefix  "new-debit"
   fo/id            m.debits/id})

(form/defsc-form NewTransaction [_this _props]
  {fo/attributes    [m.transactions/description
                     m.transactions/date
                     j.transactions/debits]
   fo/cancel-route  ["transactions"]
   fo/field-styles  {::m.transactions/account :pick-one}
   fo/field-options {::m.transactions/account
                     {::picker-options/query-key       ::m.accounts/index
                      ::picker-options/query-component AccountQuery
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.accounts/keys [id name]}]
                           {:text  (str name)
                            :value [::m.accounts/id id]})
                         (sort-by ::m.accounts/name options)))}}
   fo/id            m.transactions/id
   fo/route-prefix  "transaction-form"
   fo/subforms      {::j.transactions/debits {fo/ui NewDebit}}
   fo/title         "Transaction"})

(form/defsc-form EditForm [_this _props]
  {fo/attributes    [m.transactions/description]
   fo/cancel-route  ["transactions"]
   fo/field-styles  {::m.transactions/account :pick-one}
   fo/field-options {::m.transactions/account
                     {::picker-options/query-key       ::m.accounts/index
                      ::picker-options/query-component AccountQuery
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.accounts/keys [id name]}]
                           {:text  (str name)
                            :value [::m.accounts/id id]})
                         (sort-by ::m.accounts/name options)))}}
   fo/id            m.transactions/id
   fo/route-prefix  "edit-transaction-form"
   fo/title         "Transaction"})

(defsc CurrencyInfo
  [_this {::m.currencies/keys [name]}]
  {:ident         ::m.currencies/id
   :query         [::m.currencies/id
                   ::m.currencies/name]
   :initial-state {::m.currencies/id   nil
                   ::m.currencies/name ""}}
  (dom/div {} (str name)))

(def ui-currency-info (comp/factory CurrencyInfo {:keyfn ::m.currencies/id}))

(defsc AccountInfo
  [_this {::m.accounts/keys                   [name]
          {currency-name ::m.currencies/name} ::m.accounts/currency}]
  {:ident         ::m.accounts/id
   :query         [::m.accounts/id ::m.accounts/name
                   {::m.accounts/currency (comp/get-query CurrencyInfo)}]
   :initial-state {::m.accounts/id       nil
                   ::m.accounts/name     ""
                   ::m.accounts/currency {}}}
  (dom/div {}
    (dom/div {} (str name))
    (dom/div {} (str currency-name))))

(def ui-account-info (comp/factory AccountInfo {:keyfn ::m.accounts/id}))

(defsc DebitLine-Table
  [_this {::m.debits/keys    [value]
          ::j.debits/keys    [current-rate]
          {currency ::m.accounts/currency
           :as      account} ::m.debits/account}]
  {:ident         ::m.debits/id
   :query         [::m.debits/id
                   ::m.debits/value
                   {::m.debits/account (comp/get-query AccountInfo)}
                   {::j.debits/current-rate (comp/get-query u.links/RateValueLinkForm)}]
   :initial-state {::m.debits/id           nil
                   ::m.debits/value        0
                   ::m.debits/account      {}
                   ::j.debits/current-rate {}}}
  (ui-table-row {}
    (ui-table-cell {} (str value))
    (ui-table-cell {} (u.links/ui-currency-link currency))
    (ui-table-cell {} (u.links/ui-account-link account))
    (ui-table-cell {} (u.links/ui-rate-value-link current-rate))))

(defsc DebitLine-List
  [_this {::j.debits/keys                              [event-value]
          {currency ::m.accounts/currency :as account} ::m.debits/account
          :ui/keys                                     [debug-debits]
          :as                                          props}]
  {:ident         ::m.debits/id
   :query         [::m.debits/id
                   ::m.debits/value
                   {::m.debits/account (comp/get-query AccountInfo)}
                   ::j.debits/event-value
                   {::j.debits/current-rate (comp/get-query u.links/RateValueLinkForm)}
                   ::j.debits/current-rate-value
                   :ui/debug-debits]
   :initial-state {::m.debits/id                 nil
                   ::m.debits/value              0
                   ::m.debits/account            {}
                   ::j.debits/event-value        0
                   ::j.debits/current-rate       {}
                   ::j.debits/current-rate-value 0
                   :ui/debug-debits              show-debits-debug}}
  (ui-list-item {}
    (ui-grid {:celled true :doubling true :padded false}
      (ui-grid-row {}
        (ui-grid-column {} (u.links/ui-account-link account)))
      (ui-grid-row {}
        (ui-grid-column {:width 8 :textAlign "right"}
          (u.links/ui-debit-link props))
        (ui-grid-column {:width 8}
          (when currency
            (u.links/ui-currency-link currency))))
      (when debug-debits
        (ui-grid-row {}
          (dom/div {} "Debug section")
          (u.debug/log-props props)))
      (ui-grid-row {}
        (ui-grid-column {:width 8 :textAlign "right"}
          (str event-value))
        (ui-grid-column {:width 8} "sats")))))

(def ui-debit-list-line (comp/factory DebitLine-List {:keyfn ::m.debits/id}))

(def DebitLine (if use-table DebitLine-Table DebitLine-List))

(def ui-debit-line (comp/factory DebitLine {:keyfn ::m.debits/id}))

(defsc BodyItem
  [_this {::m.transactions/keys [date]
          ::j.transactions/keys [positive-debits negative-debits]
          :as props}]
  {:ident         ::m.transactions/id
   :query         [::m.transactions/id
                   ::m.transactions/date
                   ::m.transactions/description
                   {::j.transactions/positive-debits (comp/get-query DebitLine-List)}
                   {::j.transactions/negative-debits (comp/get-query DebitLine-List)}]
   :initial-state {::m.transactions/id              nil
                   ::m.transactions/date            nil
                   ::m.transactions/description     ""
                   ::j.transactions/positive-debits []
                   ::j.transactions/negative-debits []}}
  (dom/div :.ui.item
    (ui-segment {}
      (dom/div {} (u.links/ui-transaction-link props))
      (dom/div {}
        (if use-moment
          (ui-moment {:fromNow true :withTitle true}
            (str date))
          (str date)))
      (ui-grid {:padded false}
        (ui-grid-row {}
          (ui-grid-column {:width 8}
            (dom/div :.ui.lists
              (map ui-debit-list-line negative-debits)))
          (ui-grid-column {:width 8}
            (dom/div :.ui.lists
              (map ui-debit-list-line positive-debits))))))))

(def ui-body-item (comp/factory BodyItem {:keyfn ::m.transactions/id}))

(def new-button
  {:label  "New Transaction"
   :type   :button
   :action (fn [this] (form/create! this NewTransaction))})

(def show-controls true)

(report/defsc-report Report
  [this props]
  {ro/BodyItem            BodyItem
   ro/column-formatters   {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/columns             [m.transactions/description
                           m.transactions/date
                           j.transactions/debit-count]
   ro/control-layout      {:action-buttons [::new ::refresh]}
   ro/controls            {::new     new-button
                           ::refresh u.links/refresh-control}
   ro/initial-sort-params {:sort-by          ::m.transactions/date
                           :sortable-columns #{::m.transactions/date}
                           :ascending?       false}
   ro/machine             spr/machine
   ro/page-size           10
   ro/paginate?           true
   ro/row-pk              m.transactions/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.transactions/index
   ro/title               "Transaction Report"}
  (log/debug :Report/starting {:props props})
  (let [{:ui/keys [current-rows]} props]
    (dom/div :.ui.container.centered
      (ui-segment {}
        (ui-grid {}
          (ui-grid-row {:centered true}
            (ui-grid-column {:width 12}
              (dom/h1 :.ui.header
                (dom/span {} "Transactions")))
            (ui-grid-column {:width 4 :floated "right"}
              (ui-button-group {:compact true :floated "right"}
                (ui-button {:onClick (fn [_] (form/create! this NewTransaction))} "New")
                (ui-button {:icon "refresh" :onClick (fn [_] (control/run! this))})))))
        (when show-controls
          ((report/control-renderer this) this))
        (dom/div :.ui.container.centered
          (map ui-body-item  current-rows))))))

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
   ro/field-formatters    {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/initial-sort-params {:sort-by          ::m.transactions/date
                           :sortable-columns #{::m.transactions/date}
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
         ::j.transactions/keys [debit-count]
         :ui/keys              [debits]
         :as                   props}]
  {:ident         ::m.transactions/id
   :initial-state {::m.transactions/description ""
                   ::m.transactions/id          nil
                   ::m.transactions/date        ""
                   ::j.transactions/debit-count 0
                   :ui/debits                   {}}
   ;; :pre-merge     (u.loader/page-merger ::m.transactions/id {:ui/debits [u.t.debits/SubPage {}]})
   :query         [::m.transactions/description
                   ::m.transactions/id
                   ::m.transactions/date
                   ::j.transactions/debit-count
                   {:ui/debits (comp/get-query u.t.debits/SubPage)}]}
  (log/debug :Show/starting {:props props})
  (dom/div {}
    (if id
      (ui-segment {}
        (dom/h1 {}
          (str description))
        (dom/div {}
          (str "Debit Count: " debit-count))
        (dom/p {}
          (dom/span {}
            "Date: ")
          (dom/span {}
            (ui-moment {:fromNow true :withTitle true}
              (str date))))
        (dom/button {:classes [:.ui :.button]
                     :onClick (fn [_e]
                                (let [props (comp/props this)]
                                  (log/info :Show/clicked {:props props})
                                  (let [id (::m.transactions/id props)]
                                    (form/edit! this NewTransaction id))))}
          "Edit"))
      (ui-segment {:color "red" :inverted true} "Failed to load record"))
    (ui-segment {}
      (if debits
        (u.t.debits/ui-sub-page debits)
        (dom/p {} "Transaction debits not loaded")))))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [_] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["transactions"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (if report
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))

(defsc ShowPage
  [_this {::m.navlinks/keys     [target]
          ::m.transactions/keys [id]
          :as                   props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}
                   ::m.transactions/id nil}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}
                   ::m.transactions/id]
   :route-segment ["transaction" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))
