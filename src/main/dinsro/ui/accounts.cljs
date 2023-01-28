(ns dinsro.ui.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.ui.account-transactions :as u.account-transactions]
   [dinsro.ui.links :as u.links]))

;; [[../joins/accounts.cljc][Account Joins]]
;; [[../model/accounts.cljc][Account Models]]

(defsc CurrencyQuery
  [_this _props]
  {:query [::m.currencies/id ::m.currencies/name]
   :ident ::m.currencies/id})

(def override-form true)

(form/defsc-form NewAccountForm
  [this {::m.accounts/keys [currency name initial-value user]
         :as               props}]
  {fo/attributes    [m.accounts/name
                     m.accounts/currency
                     m.accounts/user
                     m.accounts/initial-value]
   fo/cancel-route  ["accounts"]
   fo/field-options {::m.accounts/currency {::picker-options/query-key       ::m.currencies/index
                                            ::picker-options/query-component CurrencyQuery
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
   :action (fn [this _] (form/create! this NewAccountForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.accounts/name
                        m.accounts/currency
                        m.accounts/user
                        m.accounts/initial-value
                        m.accounts/wallet
                        m.accounts/source]

   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::new new-button
                        ::refresh u.links/refresh-control}
   ro/field-formatters {::m.accounts/currency #(u.links/ui-currency-link %2)
                        ::m.accounts/user     #(u.links/ui-user-link %2)
                        ::m.accounts/name     #(u.links/ui-account-link %3)
                        ::m.accounts/wallet   #(u.links/ui-wallet-link %2)
                        ::m.accounts/source   #(u.links/ui-rate-source-link %2)}
   ro/route            "accounts"
   ro/row-actions      [(u.links/row-action-button "Delete" ::m.accounts/id mu.accounts/delete!)]
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.accounts/index
   ro/title            "Accounts"})

(report/defsc-report AdminReport
  [_this _props]
  {ro/columns          [m.accounts/name
                        m.accounts/currency
                        m.accounts/user
                        m.accounts/initial-value]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/field-formatters {::m.accounts/currency #(u.links/ui-currency-link %2)
                        ::m.accounts/user     #(u.links/ui-user-link %2)
                        ::m.accounts/name     #(u.links/ui-account-link %3)}
   ro/route            "accounts"
   ro/row-actions      [{:action
                         (fn [report-instance row-props]
                           (let [{::m.accounts/keys [id]} row-props]
                             (form/delete! report-instance ::m.accounts/id id)))
                         :label "delete"}]
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.accounts/admin-index
   ro/title            "Accounts"})

(def ui-admin-report (comp/factory AdminReport))

(report/defsc-report AccountsSubReport
  [_this _props]
  {ro/field-formatters {::m.accounts/currency #(u.links/ui-currency-link %2)
                        ::m.accounts/user     #(u.links/ui-user-link %2)
                        ::m.accounts/name     #(u.links/ui-account-link %3)}
   ro/columns          [m.accounts/name
                        m.accounts/currency
                        m.accounts/initial-value]
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.accounts/index
   ro/title            "Accounts"})

(defsc Show
  [_this {::m.accounts/keys [name currency source user wallet]
          :ui/keys          [transactions]}]
  {:route-segment ["accounts" :id]
   :query         [::m.accounts/name
                   ::m.accounts/id
                   {::m.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}
                   {::m.accounts/source (comp/get-query u.links/RateSourceLinkForm)}
                   {::m.accounts/user (comp/get-query u.links/UserLinkForm)}
                   {::m.accounts/wallet (comp/get-query u.links/WalletLinkForm)}
                   {:ui/transactions (comp/get-query u.account-transactions/SubPage)}]
   :initial-state {::m.accounts/name     ""
                   ::m.accounts/id       nil
                   ::m.accounts/currency {}
                   ::m.accounts/source   {}
                   ::m.accounts/user     {}
                   ::m.accounts/wallet   {}
                   :ui/transactions      {}}
   :ident         ::m.accounts/id
   :will-enter    (partial u.links/page-loader ::m.accounts/id ::Show)
   :pre-merge     (u.links/page-merger ::m.accounts/id {:ui/transactions u.account-transactions/SubPage})}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/dl {}
       (dom/dt {} "Name")
       (dom/dd {} (str name))
       (dom/dt {} "Currency")
       (dom/dd {} (u.links/ui-currency-link currency))
       (dom/dt {} "Source")
       (dom/dd {} (u.links/ui-rate-source-link source))
       (dom/dt {} "User")
       (dom/dd {} (u.links/ui-user-link user))
       (dom/dt {} "Wallet")
       (dom/dd {} (u.links/ui-wallet-link wallet))))
   (dom/div :.ui.segment
     (if transactions
       (u.account-transactions/ui-sub-page transactions)
       (dom/p {} "Account transactions not loaded")))))
