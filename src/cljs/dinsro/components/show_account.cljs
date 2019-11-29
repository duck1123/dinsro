(ns dinsro.components.show-account
  (:require [dinsro.events.currencies :as e.currencies]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.users :as s.users]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn-spec show-account vector?
  [account ::s.accounts/item]
  (let [user-id (get-in account [::s.accounts/user :db/id])
        user @(rf/subscribe [::e.users/item user-id])
        currency-id (get-in account [::s.accounts/currency :db/id])
        currency @(rf/subscribe [::e.currencies/item currency-id])]
    [:div
     [:p "Account: " (str account)]
     [:p "Name: " (::s.accounts/name account)]
     [:p "User: " [:a {:href (kf/path-for [:show-user-page {:id user-id}])}
                   (::s.users/name user)]]
     [:p "Currency: " [:a {:href (kf/path-for [:show-currency-page {:id currency-id}])}
                       (::s.currencies/name currency)]]
     [:button.button.is-danger "Delete"]]))
