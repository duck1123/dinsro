(ns dinsro.components.admin-index-accounts-test
  (:require
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.admin-index-accounts :as c.admin-index-accounts]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.translations :refer [tr]]))

(defcard-rg title
  [:div
   [:h1.title "Admin Index Accounts Components"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.events.admin_index_accounts_test"}
      "Admin Index Accounts Events"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.events.forms.create_account_test"}
      "Create Account Form Events"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.events.forms.admin_create_account_test"}
      "Admin Create Account Form Events"]]
    [:li
     [:a {:href "devcards.html#!/dinsro.components.forms.admin_create_account_test"}
      "Admin Create Account Form Components"]]]])

(let [account (ds/gen-key ::s.accounts/item)]
  (defcard account account)
  (defcard-rg c.admin-index-accounts/row-line
    [c.admin-index-accounts/row-line account]))

(let [accounts (ds/gen-key (s/coll-of ::s.accounts/item))]
  (comment (defcard accounts accounts))
  (defcard-rg c.admin-index-accounts/index-accounts
    [c.admin-index-accounts/index-accounts accounts]))

(defcard-rg c.admin-index-accounts/section
  "**Admin Index Rate Sources**"
  (fn []
    [error-boundary
     [c.admin-index-accounts/section]])
  {})
