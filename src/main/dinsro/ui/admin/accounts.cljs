(ns dinsro.ui.admin.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/accounts.cljc]]
;; [[../../model/accounts.cljc]]

(def index-page-key :admin-accounts)
(def model-key ::m.accounts/id)
(def override-form true)
(def show-page-key :admin-accounts-show)

(form/defsc-form NewForm
  [this {::m.accounts/keys [currency name initial-value user]
         :as               props}]
  {fo/attributes    [m.accounts/name
                     m.accounts/currency
                     m.accounts/user
                     m.accounts/initial-value]
   fo/cancel-route  ["accounts"]
   fo/field-options {::m.accounts/currency {::picker-options/query-key       ::m.currencies/index
                                            ::picker-options/query-component u.links/CurrencyLinkForm
                                            ::picker-options/options-xform
                                            (fn [_ options]
                                              (mapv
                                               (fn [{::m.currencies/keys [id name]}]
                                                 {:text  (str name)
                                                  :value [::m.currencies/id id]})
                                               (sort-by ::m.currencies/name options)))}
                     ::m.accounts/user     {::picker-options/query-key       ::m.users/index
                                            ::picker-options/query-component u.links/UserLinkForm
                                            ::picker-options/options-xform
                                            (fn [_ options]
                                              (mapv
                                               (fn [{::m.users/keys [id name]}]
                                                 {:text  (str name)
                                                  :value [::m.users/id id]})
                                               (sort-by ::m.users/name options)))}}
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
  {ro/column-formatters {::m.accounts/currency #(u.links/ui-currency-link %2)
                         ::m.accounts/user     #(u.links/ui-user-link %2)
                         ::m.accounts/name     #(u.links/ui-account-link %3)
                         ::m.accounts/source   #(u.links/ui-rate-source-link %2)
                         ::m.accounts/wallet   #(and %2 (u.links/ui-wallet-link %2))}
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
   ro/row-actions       [(u.buttons/row-action-button "Delete" model-key mu.accounts/delete!)]
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
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["accounts"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/info :Page/starting {:props props})
  (if report
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["node" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (if target
    (ui-show target)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))
