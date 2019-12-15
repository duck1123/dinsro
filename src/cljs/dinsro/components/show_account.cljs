(ns dinsro.components.show-account
  (:require [dinsro.components.debug :as c.debug]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.users :as s.users]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn user-link
  [user-id]
  (if-let [user @(rf/subscribe [::e.users/item user-id])]
    [:a {:href (kf/path-for [:show-user-page {:id user-id}])}
     (::s.users/name user)]
    [:span
     [:a {:on-click #(rf/dispatch [::e.users/do-fetch-record user-id])}
      "Not Loaded"]]))

(defn currency-link
  [currency-id]
  (if-let [currency @(rf/subscribe [::e.currencies/item currency-id])]
    [:a {:href (kf/path-for [:show-currency-page {:id currency-id}])}
     (::s.currencies/name currency)]
    [:span
     [:a {:on-click #(rf/dispatch [::e.currencies/do-fetch-record currency-id])}
      "Not Loaded"]]))

(defn delete-account-button
  [id]
  [:button.button.is-danger "Delete"])

(defn-spec show-account vector?
  [account ::s.accounts/item]
  (let [id (:db/id account)
        user-id (get-in account [::s.accounts/user :db/id])
        currency-id (get-in account [::s.accounts/currency :db/id])]
    [:<>
     [c.debug/debug-box account]
     [:h3 (::s.accounts/name account)]
     [:p
      (tr [:user-label])
      [user-link user-id]]
     [:p
      [:span "Currency: "]
      [currency-link currency-id]]
     (c.debug/hide [delete-account-button id])]))
