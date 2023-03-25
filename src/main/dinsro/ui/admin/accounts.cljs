(ns dinsro.ui.admin.accounts
  (:require
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
   [dinsro.ui.links :as u.links]))

(def override-form true)

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

(report/defsc-report AdminReport
  [_this _props]
  {ro/columns          [m.accounts/name
                        m.accounts/currency
                        m.accounts/user
                        m.accounts/initial-value
                        m.accounts/source
                        m.accounts/wallet
                        j.accounts/transaction-count]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::new     new-button
                        ::refresh u.links/refresh-control}
   ro/field-formatters {::m.accounts/currency #(u.links/ui-currency-link %2)
                        ::m.accounts/user     #(u.links/ui-user-link %2)
                        ::m.accounts/name     #(u.links/ui-account-link %3)
                        ::m.accounts/source   #(u.links/ui-rate-source-link %2)
                        ::m.accounts/wallet   #(and %2 (u.links/ui-wallet-link %2))}
   ro/route            "accounts"
   ro/row-actions      [(u.links/row-action-button "Delete" ::m.accounts/id mu.accounts/delete!)]
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.accounts/admin-index
   ro/title            "Accounts"})
