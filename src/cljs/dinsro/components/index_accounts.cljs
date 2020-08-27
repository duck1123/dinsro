(ns dinsro.components.index-accounts
  (:require
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.links :as c.links]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn row-line
  [store account]
  (let [id (:db/id account)
        initial-value (::s.accounts/initial-value account)
        currency-id (get-in account [::s.accounts/currency :db/id])
        user-id (get-in account [::s.accounts/user :db/id])]
    [:tr
     (c.debug/hide store [:td id])
     [:td (c.links/account-link store id)]
     [:td (c.links/user-link store user-id)]
     [:td (c.links/currency-link store currency-id)]
     [:td initial-value]
     (c.debug/hide store [:td [c.buttons/delete-account account]])]))

(defn index-accounts
  [store accounts]
  [:<>
   [c.debug/debug-box store accounts]
   (if-not (seq accounts)
     [:div (tr [:no-accounts])]
     [:table.table
      [:thead
       [:tr
        (c.debug/hide store [:th "Id"])
        [:th (tr [:name])]
        [:th (tr [:user-label])]
        [:th (tr [:currency-label])]
        [:th (tr [:initial-value-label])]
        (c.debug/hide store [:th (tr [:buttons])])]]
      (into [:tbody]
            (for [account accounts]
              ^{:key (:db/id account)}
              (row-line store account)))])])
