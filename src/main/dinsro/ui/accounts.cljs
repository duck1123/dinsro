(ns dinsro.ui.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
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
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.joins.users :as j.users]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.ui.accounts.transactions :as u.a.transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../joins/accounts.cljc]]
;; [[../model/accounts.cljc]]

(def index-page-key :accounts)
(def model-key ::m.accounts/id)
(def override-form true)
(def override-report false)
(def show-controls false)
(def show-page-key :accounts-show)

(form/defsc-form NewForm
  [this {::m.accounts/keys [currency name initial-value user]
         :as               props}]
  {fo/attributes     [m.accounts/name
                      m.accounts/currency
                      m.accounts/user
                      m.accounts/initial-value]
   fo/cancel-route   ["accounts"]
   fo/default-values {::m.accounts/initial-value 0}
   fo/field-options  {::m.accounts/currency {::picker-options/query-key       ::j.currencies/index
                                             ::picker-options/query-component u.links/CurrencyLinkForm
                                             ::picker-options/options-xform
                                             (fn [_ options]
                                               (mapv
                                                (fn [{::m.currencies/keys [id name]}]
                                                  {:text  (str name)
                                                   :value [::m.currencies/id id]})
                                                (sort-by ::m.currencies/name options)))}
                      ::m.accounts/user     {::picker-options/query-key       ::j.users/index
                                             ::picker-options/query-component u.links/UserLinkForm
                                             ::picker-options/options-xform
                                             (fn [_ options]
                                               (mapv
                                                (fn [{::m.users/keys [id name]}]
                                                  {:text  (str name)
                                                   :value [::m.users/id id]})
                                                (sort-by ::m.users/name options)))}}
   fo/field-styles   {::m.accounts/currency :pick-one
                      ::m.accounts/user     :pick-one}
   fo/id             m.accounts/id
   fo/route-prefix   "new-account"
   fo/title          "Create Account"}
  (if override-form
    (form/render-layout this props)
    (dom/div :.ui.segment
      (dom/p {} (str "Account: " name))
      (dom/p {} (str "Initial Value: " initial-value))
      (dom/p {} "Currency: " (u.links/ui-currency-link currency))
      (dom/p {} (str "User: " user)))))

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
   ro/row-actions       [(u.buttons/row-action-button "Delete" ::m.accounts/id mu.accounts/delete!)]
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

(def show-transactions false)

(defsc Show
  [_this {::m.accounts/keys [name currency source wallet]
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
   :pre-merge         (u.loader/page-merger ::m.accounts/id {:ui/transactions [u.a.transactions/Report {}]})
   :query             [::m.accounts/name
                       ::m.accounts/id
                       {::m.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}
                       {::m.accounts/source (comp/get-query u.links/RateSourceLinkForm)}
                       {::m.accounts/wallet (comp/get-query u.links/WalletLinkForm)}
                       {:ui/transactions (comp/get-query u.a.transactions/Report)}]}
  (log/info :Show/starting {:props props})
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
            (dom/p {} "No Transactions")))))))

(def ui-show (comp/factory Show))

(defsc ShowPage
  [_this {::m.navlinks/keys [id target]
          :as               props}]
  {:componentDidMount (fn [this]
                        (let [props (comp/props this)]
                          (log/info :ShowPage/mounted {:this this :props props})))
   :ident             (fn [] [::m.navlinks/id show-page-key])
   :initial-state     {::m.accounts/id nil
                       ::m.navlinks/id show-page-key
                       ::m.navlinks/target      {}}
   :query             [::m.accounts/id
                       ::m.navlinks/id
                       {::m.navlinks/target (comp/get-query Show)}]
   :route-segment     ["account" :id]
   :will-enter        (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (dom/div {}
      (dom/div :.ui.segment "Failed to load record")
      (u.debug/log-props props))))

(defsc IndexPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["accounts"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/trace :Page/starting {:props props})
  (dom/div {}
    (ui-report report)))
