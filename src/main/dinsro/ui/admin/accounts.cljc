(ns dinsro.ui.admin.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.admin.accounts :as u.f.a.accounts]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/accounts.cljc]]
;; [[../../model/accounts.cljc]]

(def index-page-id :admin-accounts)
(def model-key o.accounts/id)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-id :admin-accounts-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.accounts/delete!))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.a.accounts/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.accounts/currency #(u.links/ui-admin-currency-link %2)
                         o.accounts/user     #(u.links/ui-admin-user-link %2)
                         o.accounts/name     #(u.links/ui-admin-account-link %3)
                         o.accounts/source   #(u.links/ui-admin-rate-source-link %2)
                         o.accounts/wallet   #(and %2 (u.links/ui-admin-wallet-link %2))}
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
   :initial-state (fn [props]
                    {o.accounts/name     ""
                     o.accounts/id       (model-key props)
                     o.accounts/currency (comp/get-initial-state u.links/CurrencyLinkForm {})
                     o.accounts/source   (comp/get-initial-state u.links/RateSourceLinkForm {})
                     o.accounts/wallet   (comp/get-initial-state u.links/WalletLinkForm {})})
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
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["accounts"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/info :Page/starting {:props props})
  (if report
    (ui-report report)
    (u.debug/load-error props "index page")))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["account" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Accounts"
   o.navlinks/description   "Admin page of all accounts"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Admin page for account"
   o.navlinks/label         "Accounts"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
