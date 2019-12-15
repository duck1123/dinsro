(ns dinsro.components.index-accounts
  (:require [dinsro.components.buttons :as c.buttons]
            [dinsro.components.links :as c.links]
            [dinsro.events.debug :as e.debug]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.translations :refer [tr]]
            [dinsro.views.show-account :as v.show-account]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn row-line
  [account]
  (let [id (:db/id account)
        name (::s.accounts/name account)
        initial-value (::s.accounts/initial-value account)
        currency-id (get-in account [::s.accounts/currency :db/id])
        user-id (get-in account [::s.accounts/user :db/id])]
    [:tr
     [:td [c.links/account-link id]]
     [:td [c.links/user-link user-id]]
     [:td [c.links/currency-link currency-id]]
     [:td initial-value]
     (when @(rf/subscribe [::e.debug/shown?]) [:td [c.buttons/delete-account account]])]))

(defn index-accounts
  [accounts]
  [:<>
   (if-not (seq accounts)
     [:div (tr [:no-accounts])]
     [:table.table
      [:thead
       [:tr
        [:th (tr [:name])]
        [:th (tr [:user-label])]
        [:th (tr [:currency-label])]
        [:th (tr [:initial-value-label])]
        (when @(rf/subscribe [::e.debug/shown?]) [:th (tr [:buttons])])]]
      (into [:tbody]
            (for [account accounts]
              ^{:key (:db/id account)}
              (row-line account)))])])
