(ns dinsro.views-test
  (:require
   dinsro.views.about-test
   dinsro.views.admin-test
   dinsro.views.index-accounts-test
   dinsro.views.index-transactions-test
   dinsro.views.login-test
   ;; dinsro.views.registration-test
   dinsro.views.settings-test
   dinsro.views.show-currency-test
   [devcards.core :as dc :refer-macros [defcard-rg]]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Views"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]]

   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.views.about_test"}
      "About View"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.views.admin_test"}
      "Admin View"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.views.index_accounts_test"}
      "Index Accounts View"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.views.index_transactions_test"}
      "Index Transactions View"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.views.login_test"}
      "Login View"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.views.settings_test"}
      "Settings View"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.views.show_currency_test"}
      "Show Currency View"]]]])
