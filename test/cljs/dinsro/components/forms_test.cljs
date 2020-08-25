(ns dinsro.components.forms-test
  (:require
   ;; dinsro.components.forms.add-currency-rate-test
   ;; [dinsro.components.forms.add-user-account-test]
   [dinsro.components.forms.add-user-transaction-test]
   ;; [dinsro.components.forms.create-account-test]
   [dinsro.components.forms.create-transaction-test]
   ;; [dinsro.components.forms.registration-test]
   [dinsro.components.forms.settings-test]
   [devcards.core :as dc :refer-macros [defcard-rg]]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Form Components"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.events_test"}
      "Events"]]]

   [:ul.box

    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms.add_user_account_test"}
      "Add User Account Form Components"]]

    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms.add_user_transaction_test"}
      "Add User Transaction Form Components"]]

    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms.admin_create_account_test"}
      "Admin Create Account Form Components"]]

    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms.create_transaction_test"}
      "Create Transaction Form Components"]]

    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms.registration_test"}
      "Registration Form Components"]]

    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms.settings_test"}
      "Settings Form Components"]]]])
