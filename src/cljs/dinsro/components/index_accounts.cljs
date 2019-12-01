(ns dinsro.components.index-accounts
  (:require [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.users :as s.users]
            [dinsro.views.show-account :as v.show-account]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn row-line
  [account]
  (let [{:keys [db/id
                dinsro.spec.accounts/currency
                dinsro.spec.accounts/name
                dinsro.spec.accounts/user]} account
        initial-value (::s.accounts/initial-value account)
        currency-id (:db/id currency)
        user-id (:db/id user)
        user @(rf/subscribe [::e.users/item user-id])
        currency @(rf/subscribe [::e.currencies/item currency-id])]
    [:div.box
     [:p [:a {:href (kf/path-for [:show-account-page {:id id}])} name]]
     [:p
      "User: "
      (if user
        [:a {:href (kf/path-for [:show-user-page {:id user-id}])}
         (::s.users/name user)]
        [:span "Not Loaded"])]
     [:p
      "Currency: "
      (if currency
        [:a {:href (kf/path-for [:show-currency-page {:id currency-id}])}
         (::s.currencies/name currency)]
        [:span "Not Loaded"])]
     [:p "Initial Value: " initial-value]
     [:a.button.is-danger
      {:on-click #(rf/dispatch [::e.accounts/do-delete-account id])}
      "Delete"]]))

(defn index-accounts
  [accounts]
  [:div.box
   (if-not (seq accounts)
     [:div "No Accounts"]
     (into [:div.section]
           (for [account accounts]
             ^{:key (:db/id account)}
             (row-line account))))])
