(ns dinsro.ui.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.joins.users :as j.users]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.ui.accounts.debits :as u.a.debits]
   [dinsro.ui.accounts.transactions :as u.a.transactions]
   [dinsro.ui.links :as u.links]))

;; [[../joins/accounts.cljc][Account Joins]]
;; [[../model/accounts.cljc][Account Models]]

(def override-form true)

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

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.accounts/name
                        m.accounts/currency
                        m.accounts/user
                        m.accounts/initial-value
                        m.accounts/wallet
                        m.accounts/source
                        j.accounts/debit-count]

   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::new new-button
                        ::refresh u.links/refresh-control}
   ro/field-formatters {::m.accounts/currency #(u.links/ui-currency-link %2)
                        ::m.accounts/user     #(u.links/ui-user-link %2)
                        ::m.accounts/name     #(u.links/ui-account-link %3)
                        ::m.accounts/wallet   #(when %2 (u.links/ui-wallet-link %2))
                        ::m.accounts/source   #(u.links/ui-rate-source-link %2)}
   ro/route            "accounts"
   ro/row-actions      [(u.links/row-action-button "Delete" ::m.accounts/id mu.accounts/delete!)]
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.accounts/index
   ro/title            "Accounts"})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.transactions/SubPage
    u.a.debits/SubPage]})

(def menu-items
  [{:key "transactions"
    :name "Transactions"
    :route "dinsro.ui.accounts.transactions/SubPage"}
   {:key   "debits"
    :name  "Debits"
    :route "dinsro.ui.accounts.debits/SubPage"}])

(defsc Show
  [_this {::m.accounts/keys [id name currency source user wallet]
          :ui/keys          [router]}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/name     ""
                   ::m.accounts/id       nil
                   ::m.accounts/currency {}
                   ::m.accounts/source   {}
                   ::m.accounts/user     {}
                   ::m.accounts/wallet   {}
                   :ui/router            {}}
   :pre-merge     (u.links/page-merger ::m.accounts/id {:ui/router Router})
   :query         [::m.accounts/name
                   ::m.accounts/id
                   {::m.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}
                   {::m.accounts/source (comp/get-query u.links/RateSourceLinkForm)}
                   {::m.accounts/user (comp/get-query u.links/UserLinkForm)}
                   {::m.accounts/wallet (comp/get-query u.links/WalletLinkForm)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["accounts" :id]
   :will-enter    (partial u.links/page-loader ::m.accounts/id ::Show)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/h1 {} (str name))
     (dom/dl {}
       (dom/dt {} "Currency")
       (dom/dd {} (u.links/ui-currency-link currency))
       (dom/dt {} "Source")
       (dom/dd {} (u.links/ui-rate-source-link source))
       (dom/dt {} "User")
       (dom/dd {} (u.links/ui-user-link user))
       (dom/dt {} "Wallet")
       (dom/dd {} (u.links/ui-wallet-link wallet))))
   (u.links/ui-nav-menu {:menu-items menu-items :id id})
   ((comp/factory Router) router)))
