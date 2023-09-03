(ns dinsro.ui.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.collections.table.ui-table :refer [ui-table]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-body :refer [ui-table-body]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-cell :refer [ui-table-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header :refer [ui-table-header]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header-cell :refer [ui-table-header-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-row :refer [ui-table-row]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.navlinks :as m.navlinks :refer [defroute]]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.accounts.transactions :as u.a.transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

;; [[../joins/accounts.cljc]]
;; [[../model/accounts.cljc]]
;; [[../options/accounts.cljc]]

(def index-page-id :accounts)
(def model-key ::m.accounts/id)
(def override-form true)
(def override-report true)
(def parent-router-id :root)
(def show-controls false)
(def show-transactions true)
(def show-page-key :accounts-show)

(def create-action
  (u.buttons/row-action-button "Create" model-key mu.accounts/create!))

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.accounts/delete!))

;; Create form for accounts as a user
(form/defsc-form NewForm
  [this {currency      o.accounts/currency
         name          o.accounts/name
         initial-value o.accounts/initial-value
         :as           props}]
  {fo/action-buttons [::create]
   fo/attributes     [m.accounts/name
                      m.accounts/currency
                      m.accounts/user
                      m.accounts/initial-value]
   fo/cancel-route   ["accounts"]
   fo/controls       (merge form/standard-controls {::create create-action})
   fo/default-values {o.accounts/initial-value 0}
   fo/field-options  {o.accounts/currency u.pickers/currency-picker
                      o.accounts/user     u.pickers/user-picker}
   fo/field-styles   {o.accounts/currency :pick-one
                      o.accounts/user     :pick-one}
   fo/id             m.accounts/id
   fo/route-prefix   "new-account"
   fo/title          "Create Account"}
  (if override-form
    (form/render-layout this props)
    (ui-segment {}
      (dom/div {}
        (str "Account: " name))
      (dom/div {}
        (str "Initial Value: " initial-value))
      (dom/div {}
        "Currency: " (u.links/ui-currency-link currency)))))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewForm))})

(defsc DebitLine
  [_this {::m.debits/keys [value]}]
  {:ident         ::m.debits/id
   :initial-state {::m.debits/id    nil
                   ::m.debits/value 0}
   :query         [::m.debits/id
                   ::m.debits/value]}
  (ui-list-item {}
    (str value)))

(def ui-debit-line (comp/factory DebitLine {:keyfn ::m.debits/id}))

(defsc BodyItem
  [_this {::m.accounts/keys [currency initial-value wallet]
          ::j.accounts/keys [debit-count]
          :as               props}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/id            nil
                   ::m.accounts/name          ""
                   ::m.accounts/currency      {}
                   ::m.accounts/initial-value 0
                   ::m.accounts/wallet        {}
                   ::j.accounts/debit-count   0
                   ::j.accounts/debits        []}
   :query         [::m.accounts/id
                   ::m.accounts/name
                   {::m.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}
                   ::m.accounts/initial-value
                   {::m.accounts/wallet (comp/get-query u.links/WalletLinkForm)}
                   ::j.accounts/debit-count
                   {::j.accounts/debits (comp/get-query DebitLine)}]}
  (ui-table-row {}
    (ui-table-cell {} (u.links/ui-account-link props))
    (ui-table-cell {} (u.links/ui-currency-link currency))
    (ui-table-cell {} (str initial-value))
    (ui-table-cell {} (when wallet (u.links/ui-wallet-link wallet)))
    (ui-table-cell {} (str debit-count))))

(def ui-body-item (comp/factory BodyItem {:keyfn ::m.accounts/id}))

(report/defsc-report Report
  [this props]
  {ro/BodyItem          BodyItem
   ro/column-formatters {::m.accounts/currency #(u.links/ui-currency-link %2)
                         ::m.accounts/user     #(u.links/ui-user-link %2)
                         ::m.accounts/name     #(u.links/ui-account-link %3)
                         ::m.accounts/wallet   #(when %2 (u.links/ui-wallet-link %2))
                         ::m.accounts/source   #(u.links/ui-rate-source-link %2)}
   ro/columns           [m.accounts/name
                         m.accounts/currency
                         m.accounts/initial-value
                         m.accounts/wallet
                         j.accounts/debit-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.accounts/index
   ro/title             "Accounts"}
  (let [{:ui/keys [current-rows]} props]
    (if override-report
      (report/render-layout this)
      (dom/div {}
        (ui-segment {}
          (dom/h1 {} "Accounts"))
        (when show-controls ((report/control-renderer this) this))
        (ui-table {}
          (ui-table-header {}
            (ui-table-row {}
              (ui-table-header-cell {} "Name")
              (ui-table-header-cell {} "Currency")
              (ui-table-header-cell {} "Initial Value")
              (ui-table-header-cell {} "Wallet")
              (ui-table-header-cell {} "Debit Count")))
          (ui-table-body {}
            (map ui-body-item current-rows)))))))

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.accounts/keys [id name currency source wallet]
          :ui/keys          [transactions]
          :as               props}]
  {:componentDidMount #(report/start-report! % u.a.transactions/Report {:route-params (comp/props %)})
   :ident             ::m.accounts/id
   :initial-state     {::m.accounts/name     ""
                       ::m.accounts/id       nil
                       ::m.accounts/currency {}
                       ::m.accounts/source   {}
                       ::m.accounts/wallet   {}
                       :ui/transactions      {}}
   :pre-merge         (u.loader/page-merger model-key
                        {:ui/transactions [u.a.transactions/Report {}]})
   :query             [::m.accounts/name
                       ::m.accounts/id
                       {::m.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}
                       {::m.accounts/source (comp/get-query u.links/RateSourceLinkForm)}
                       {::m.accounts/wallet (comp/get-query u.links/WalletLinkForm)}
                       {:ui/transactions (comp/get-query u.a.transactions/Report)}]}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} (str name))
        (dom/dl {}
          (dom/dt {} "Currency")
          (dom/dd {}
            (when currency
              (u.links/ui-currency-link currency)))
          (dom/dt {} "Source")
          (dom/dd {}
            (when source
              (u.links/ui-rate-source-link source)))
          (dom/dt {} "Wallet")
          (dom/dd {}
            (when wallet
              (u.links/ui-wallet-link wallet)))))
      (when show-transactions
        (ui-segment {}
          (if transactions
            (u.a.transactions/ui-report transactions)
            (dom/div {}
              (dom/p {} "No Transactions"))))))
    (u.debug/load-error props "show account record")))

(def ui-show (comp/factory Show))

(defsc ShowPage
  [_this {::m.navlinks/keys [id target]
          :as               props}]
  {:ident             (fn [] [::m.navlinks/id show-page-key])
   :initial-state     {::m.accounts/id     nil
                       ::m.navlinks/id     show-page-key
                       ::m.navlinks/target {}}
   :query             [::m.accounts/id
                       ::m.navlinks/id
                       {::m.navlinks/target (comp/get-query Show)}]
   :route-segment     ["account" :id]
   :will-enter        (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "show account page")))

(defsc IndexPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["accounts"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/trace :Page/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Accounts"
   o.navlinks/description   "An index of all accounts for a user"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role :user})

(defroute show-page-key
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Show page for an account"
   o.navlinks/label         "Show Accounts"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role :user})
