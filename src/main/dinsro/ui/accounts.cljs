(ns dinsro.ui.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

(defsc RefRow
  [_this props]
  {:ident ::m.accounts/id
   :query [::m.accounts/id
           ::m.accounts/name
           ::m.accounts/currency]}
  (dom/tr {}
    (dom/td (u.links/ui-account-link props))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.accounts/id}))

(defn ref-table
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "Name")))
     (dom/tbody {}
       (for [tx value]
         (ui-ref-row tx))))))

(def render-ref-table (render-field-factory ref-table))

(defsc CurrencyQuery
  [_this _props]
  {:query [::m.currencies/id ::m.currencies/name]
   :ident ::m.currencies/id})

(def override-form true)

(form/defsc-form AccountForm
  [this {::m.accounts/keys [currency name initial-value user]
         :as               props}]
  {fo/id           m.accounts/id
   fo/subforms     {::m.accounts/user         {fo/ui u.links/UserLinkForm}
                    ::m.accounts/currency     {fo/ui u.links/CurrencyLinkForm}
                    ::m.accounts/transactions {fo/ui u.links/TransactionLinkForm}}
   fo/field-styles {::m.accounts/transactions :link-list
                    ::m.accounts/user         :link}
   fo/attributes   [m.accounts/name
                    m.accounts/currency
                    m.accounts/user
                    m.accounts/initial-value
                    j.accounts/transactions]
   fo/cancel-route ["accounts"]
   fo/route-prefix "account"
   fo/title        "Edit Account"}
  (if override-form
    (form/render-layout this props)
    (dom/div :.ui
      (dom/p {} (str "Account: " name))
      (dom/p {} (str "Initial Value: " initial-value))
      (dom/p {} (str "Currency: " currency))
      (dom/p {} (str "User: " user)))))

(form/defsc-form NewAccountForm
  [this {::m.accounts/keys [currency name initial-value user]
         :as               props}]
  {fo/id            m.accounts/id
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

   fo/field-styles {::m.accounts/currency :pick-one
                    ::m.accounts/user     :pick-one}
   fo/attributes   [m.accounts/name
                    m.accounts/currency
                    m.accounts/user
                    m.accounts/initial-value]
   fo/cancel-route ["accounts"]
   fo/route-prefix "new-account"
   fo/title        "Create Account"}
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

(report/defsc-report AccountsReport
  [_this _props]
  {ro/form-links       {::m.accounts/name AccountForm}
   ro/field-formatters
   {::m.accounts/currency (fn [_this props] (u.links/ui-currency-link props))
    ::m.accounts/user     (fn [_this props] (u.links/ui-user-link props))}
   ro/columns          [m.accounts/name
                        m.accounts/currency
                        m.accounts/user
                        m.accounts/initial-value]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/route            "accounts"
   ro/row-actions      [{:action
                         (fn [report-instance row-props]
                           (let [{::m.accounts/keys [id]} row-props]
                             (form/delete! report-instance ::m.accounts/id id)))
                         :label "delete"}]
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.accounts/index
   ro/title            "Accounts"})

(report/defsc-report AdminIndexAccountsReport
  [_this _props]
  {ro/form-links       {::m.accounts/name AccountForm}
   ro/field-formatters
   {::m.accounts/currency (fn [_this props] (u.links/ui-currency-link props))
    ::m.accounts/user     (fn [_this props] (u.links/ui-user-link props))}
   ro/columns          [m.accounts/name
                        m.accounts/currency
                        m.accounts/user
                        m.accounts/initial-value]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/route            "accounts"
   ro/row-actions      [{:action
                         (fn [report-instance row-props]
                           (let [{::m.accounts/keys [id]} row-props]
                             (form/delete! report-instance ::m.accounts/id id)))
                         :label "delete"}]
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.accounts/admin-index
   ro/title            "Accounts"})

(def ui-admin-index-accounts (comp/factory AdminIndexAccountsReport))

(report/defsc-report AccountsSubReport
  [_this _props]
  {ro/form-links       {::m.accounts/name AccountForm}
   ro/field-formatters
   {::m.accounts/currency (fn [_this props] (u.links/ui-currency-link props))
    ::m.accounts/user     (fn [_this props] (u.links/ui-user-link props))}
   ro/columns          [m.accounts/name
                        m.accounts/currency
                        m.accounts/initial-value]
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.accounts/index
   ro/title            "Accounts"})
