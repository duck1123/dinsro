(ns dinsro.components-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.components :as c]
   [dinsro.components.admin-index-accounts-test]
   [dinsro.components.admin-index-categories-test]
   [dinsro.components.admin-index-rate-sources-test]
   [dinsro.components.currency-rates-test]
   [dinsro.components.index-transactions-test]
   [dinsro.components.rate-chart-test]
   [dinsro.components.show-account-test]
   [dinsro.components.show-currency-test]
   [dinsro.components.status-test]
   [devcards.core :as dc :refer-macros [defcard-rg deftest]]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Components"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms_test"}
      "Form Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.events_test"}
      "Events"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.views_test"}
      "Views"]]]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components.admin_index_accounts_test"}
      "Admin Index Accounts Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components.admin_index_categories_test"}
      "Admin Index Categories Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components.admin_index_rate_sources_test"}
      "Admin Index Rate Sources Components"]]

    [:li
     [:a {:href "devcards.html#!/dinsro.components.buttons_test"}
      "Button Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components.currency_rates_test"}
      "Currency Rate Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components.index_transactions_test"}
      "Index Transactions Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components.rate_chart_test"}
      "Rate Chart Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components.show_account_test"}
      "Show Account Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components.show_currency_test"}
      "Show Currency Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components.show_user_test"}
      "Show User Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components.status_test"}
      "Status Components"]]]])

(defcard-rg checkbox-input
  (with-redefs [rf/subscribe (fn [x] (timbre/spy :info x))]
    (r/as-element
     [:div
      [:p "Foo"]
      #_[c/checkbox-input "foo" :foo]])))

#_(defcard-rg account-selector
  [c/account-selector "foo" :foo]
  )

(deftest account-selector
  (let [label "foo"
        field :foo]
    (is (vector? (c/account-selector label field)))))
