(ns dinsro.components.forms.add-user-transaction-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.add-user-transaction :as c.f.add-user-transaction]
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.spec :as ds]
   [dinsro.translations :refer [tr]]))

(defcard-rg title
  [:div
   [:h1.title "Add User Transaction Form Components"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms_test"}
      "Form Components"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.events.forms.add_user_transaction_test"}
      "Add User Transaction Forms Events"]]]])

(defcard form-data
  (ds/gen-key ::e.f.add-user-transaction/form-data))

(defcard-rg form
  ;; "Create a transaction when the user is already provided"
  (fn []
    [error-boundary
     [c.f.add-user-transaction/form-shown]]))
