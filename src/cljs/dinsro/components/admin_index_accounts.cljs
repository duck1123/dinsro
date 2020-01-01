(ns dinsro.components.admin-index-accounts
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.forms.admin-create-account :as c.f.admin-create-account]
            [dinsro.components.links :as c.links]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.forms.create-account :as e.f.create-account]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn row-line
  [account]
  (let [id (:db/id account)
        initial-value (::s.accounts/initial-value account)
        currency-id (get-in account [::s.accounts/currency :db/id])
        user-id (get-in account [::s.accounts/user :db/id])]
    [:tr
     (c.debug/hide [:td id])
     [:td (c.links/account-link id)]
     [:td (c.links/user-link user-id)]
     [:td (c.links/currency-link currency-id)]
     [:td initial-value]
     (c.debug/hide [:td [c.buttons/delete-account account]])]))

(s/fdef row-line
  :args (s/cat :account ::s.accounts/item)
  :ret vector?)

(defn index-accounts
  [accounts]
  [:<>
   [c.debug/debug-box accounts]
   (if-not (seq accounts)
     [:div (tr [:no-accounts])]
     [:table.table
      [:thead
       [:tr
        (c.debug/hide [:th "Id"])
        [:th (tr [:name])]
        [:th (tr [:user-label])]
        [:th (tr [:currency-label])]
        [:th (tr [:initial-value-label])]
        (c.debug/hide [:th (tr [:buttons])])]]
      (into [:tbody]
            (for [account accounts]
              ^{:key (:db/id account)}
              (row-line account)))])])

(s/fdef index-accounts
  :args (s/cat :accounts (s/coll-of ::s.accounts/item))
  :ret vector?)

(defn section
  []
  (let [accounts @(rf/subscribe [::e.accounts/items])
        state @(rf/subscribe [::e.accounts/do-fetch-index-state])]
    [:div.box
     [:h1
      (tr [:index-accounts])
      [c/show-form-button ::e.f.create-account/shown? ::e.f.create-account/set-shown?]]
     [c.f.admin-create-account/form]
     [:hr]
     (condp = state
       :invalid [:p "Invalid"]
       :loaded  [index-accounts accounts]
       [:p "Unknown state: " state])]))
