(ns dinsro.ui.index-accounts
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(defn row-line
  [store account]
  (let [id (:db/id account)
        initial-value (::m.accounts/initial-value account)
        currency-id (get-in account [::m.accounts/currency :db/id])
        user-id (get-in account [::m.accounts/user :db/id])]
    [:tr
     (u.debug/hide store [:td id])
     [:td (u.links/account-link store id)]
     [:td (u.links/user-link store user-id)]
     [:td (u.links/currency-link store currency-id)]
     [:td initial-value]
     (u.debug/hide store [:td [u.buttons/delete-account store account]])]))

(defn section
  [store accounts]
  (if-not (seq accounts)
    [:div (tr [:no-accounts])]
    [:table.table
     [:thead
      [:tr
       (u.debug/hide store [:th "Id"])
       [:th (tr [:name])]
       [:th (tr [:user-label])]
       [:th (tr [:currency-label])]
       [:th (tr [:initial-value-label])]
       (u.debug/hide store [:th (tr [:buttons])])]]
     (into [:tbody]
           (for [account accounts]
             ^{:key (:db/id account)}
             (row-line store account)))]))
