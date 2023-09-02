(ns dinsro.ui.admin.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

;; [[../../joins/accounts.cljc]]
;; [[../../model/accounts.cljc]]

(def index-page-id :admin-accounts)
(def model-key ::m.accounts/id)
(def override-form true)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-key :admin-accounts-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.accounts/delete!))

(form/defsc-form NewForm
  [this {::m.accounts/keys [currency name initial-value user]
         :as               props}]
  {fo/attributes    [m.accounts/name
                     m.accounts/currency
                     m.accounts/user
                     m.accounts/initial-value]
   fo/cancel-route  ["accounts"]
   fo/field-options {::m.accounts/currency u.pickers/admin-currency-picker
                     ::m.accounts/user     u.pickers/admin-user-picker}
   fo/field-styles  {::m.accounts/currency :pick-one
                     ::m.accounts/user     :pick-one}
   fo/id            m.accounts/id
   fo/route-prefix  "new-account"
   fo/title         "Create Account"}
  (if override-form
    (form/render-layout this props)
    (dom/div :.ui
      (dom/p {} (str "Account: " name))
      (dom/p {} (str "Initial Value: " initial-value))
      (dom/p {} (str "Currency: " currency))
      (dom/p {} (str "User: " user)))))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.accounts/currency #(u.links/ui-admin-currency-link %2)
                         ::m.accounts/user     #(u.links/ui-admin-user-link %2)
                         ::m.accounts/name     #(u.links/ui-admin-account-link %3)
                         ::m.accounts/source   #(u.links/ui-admin-rate-source-link %2)
                         ::m.accounts/wallet   #(and %2 (u.links/ui-admin-wallet-link %2))}
   ro/columns           [m.accounts/name
                         m.accounts/currency
                         m.accounts/user
                         m.accounts/initial-value
                         m.accounts/source
                         m.accounts/wallet
                         j.accounts/transaction-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.accounts/admin-index
   ro/title             "Accounts"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.accounts/keys [name currency source wallet]
          :as               props}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/name     ""
                   ::m.accounts/id       nil
                   ::m.accounts/currency {}
                   ::m.accounts/source   {}
                   ::m.accounts/wallet   {}}
   :pre-merge     (u.loader/page-merger model-key {})
   :query         [::m.accounts/name
                   ::m.accounts/id
                   {::m.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}
                   {::m.accounts/source (comp/get-query u.links/RateSourceLinkForm)}
                   {::m.accounts/wallet (comp/get-query u.links/WalletLinkForm)}]}
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
            (u.links/ui-wallet-link wallet)))))))

(def ui-show (comp/factory Show))

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
  (log/info :Page/starting {:props props})
  (if report
    (ui-report report)
    (u.debug/load-error props "index page")))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["account" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if target
    (ui-show target)
    (u.debug/load-error props "admin show account")))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Accounts"
   o.navlinks/description   "Admin page of all accounts"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-key
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Admin page for account"
   o.navlinks/label         "Accounts"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
