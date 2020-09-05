(ns dinsro.cards
  (:require
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def common-headers
  [["dinsro.components_test"
    "Components"]
   ["dinsro.components.forms_test"
    "Form Components"]
   ["dinsro.events_test"
    "Events"]
   ["dinsro.spec_test"
    "Specs"]
   ["dinsro.spec.actions_test"
    "Action Specs"]
   ["dinsro.views_test"
    "Views"]])

(def admin-component-headers
  [["dinsro.components.admin_index_accounts_test"
    "Admin Index Accounts Components"]
   ["dinsro.components.admin_index_categories_test"
    "Admin Index Categories Components"]
   ["dinsro.components.admin_index_rate_sources_test"
    "Admin Index Rate Sources Components"]])

(def components-headers
  [["dinsro.components.buttons_test"
    "Button Components"]
   ["dinsro.components.currency_rates_test"
    "Currency Rate Components"]
   ["dinsro.components.index_transactions_test"
    "Index Transactions Components"]
   ["dinsro.components.rate_chart_test"
    "Rate Chart Components"]
   ["dinsro.components.show_account_test"
    "Show Account Components"]
   ["dinsro.components.show_currency_test"
    "Show Currency Components"]
   ["dinsro.components.show_transaction_test"
    "Show Transaction Components"]
   ["dinsro.components.show_user_test"
    "Show User Components"]
   ["dinsro.components.status_test"
    "Status Components"]])

(def form-component-headers
  [["dinsro.components.forms.add_user_account_test"
    "Add User Account Form Components"]
   ["dinsro.components.forms.add_user_transaction_test"
    "Add User Transaction Form Components"]
   ["dinsro.components.forms.admin_create_account_test"
    "Admin Create Account Form Components"]
   ["dinsro.components.forms.create_transaction_test"
    "Create Transaction Form Components"]
   ["dinsro.components.forms.registration_test"
    "Registration Form Components"]
   ["dinsro.components.forms.settings_test"
    "Settings Form Components"]])

(def events-headers
  [["dinsro.events.accounts_test"
    "Account Events"]
   ["dinsro.events.admin_index_accounts_test"
    "Admin Index Account Events"]
   ["dinsro.events.rates_test"
    "Rate Events"]
   ["dinsro.events.show_account_test"
    "Show Account Events"]
   ["dinsro.events.transactions_test"
    "Transactions Events"]])

(def form-headers
  [["dinsro.components.forms.add_user_account_test"
    "Add User Account Forms Components"]
   ["dinsro.components.forms.registration_test"
    "Registration Component"]])

(def spec-headers
  [["dinsro.spec.accounts_test"
    "Account Specs"]
   ["dinsro.spec.currencies_test"
    "Currency Specs"]
   ["dinsro.spec.rates_test"
    "Rate Specs"]
   ["dinsro.spec.transactions_test"
    "Transaction Specs"]
   ["dinsro.spec.users_test"
    "User Specs"]])

(def action-spec-headers
  [["dinsro.spec.actions.accounts_test"
    "Account Actions Specs"]
   ["dinsro.spec.actions.authentication_test"
    "Authentication Actions Specs"]
   ;; ["dinsro.spec.actions.categories_test"
   ;;  "Category Actions Specs"]
   ["dinsro.spec.actions.rate_sources_test"
    "Rate Source Actions Specs"]
   ["dinsro.spec.actions.rates_test"
    "Rate Actions Specs"]])

(def views-headers
  [["dinsro.views.registration_test"
    "Registration View"]])

(def form-event-headers
  [
   ["dinsro.events.forms.add_user_account_test"
    "Add User Account Forms Events"]
   ["dinsro.events.forms.add_user_transaction_test"
    "Add User Transaction Forms Events"]
   ["dinsro.events.forms.admin_create_account_test"
    "Admin Create Account Form Events"]
   ["dinsro.events.forms.create_account_test"
    "Create Account Form Events"]
   ["dinsro.events.forms.registration_test"
     "Registration Form Events"]])

(defn link-box
  [keys]
  [:ul.box
   (map
    (fn [[path title]]
      [:li {:key path}
       [:a {:href
            (str
             "devcards.html#!/"
             path)}
        title]])
    keys)])

(defn card-body
  [title filters]
  [:div
   [:h1.title title]
   [:p (pr-str filters)]
   [link-box common-headers]
   ;; [link-box components-headers]
   ;; [link-box admin-component-headers]
   ;; [link-box form-component-headers]
   ;; [link-box events-headers]
   ;; [link-box form-headers]
   ;; [link-box form-event-headers]
   ;; [link-box spec-headers]
   ;; [link-box action-spec-headers]
   ;; [link-box views-headers]
   ])

(defmacro header
  [title filters]
  `(devcards.core/defcard-rg title
     (fn [] (dinsro.cards/card-body ~title ~filters))))
