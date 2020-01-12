(ns dinsro.components.index-accounts
  (:require
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.links :as c.links]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.translations :refer [tr]]
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
