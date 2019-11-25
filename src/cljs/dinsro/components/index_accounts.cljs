(ns dinsro.components.index-accounts
  (:require [dinsro.events.accounts :as e.accounts]
            [dinsro.spec.accounts :as s.accounts]
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
        user-id (:db/id user)]
    [:div.column
     {:style {:border        "1px black solid"
              :margin-bottom "15px"}}
     [:p id]
     [:p [:a {:href (kf/path-for [:show-account-page {:id id}])} name]]
     [:p "User: " [:a {:href (kf/path-for [:show-user-page {:id user-id}])} user-id]]
     [:p "Currency: " [:a {:href (kf/path-for [:show-currency-page {:id currency-id}])} currency-id]]
     [:p "Initial Value: " initial-value]
     [:pre (str account)]
     [:a.button {:on-click #(rf/dispatch [::e.accounts/do-delete-account id])} "Delete"]]))

(defn index-accounts
  [accounts]
  [:div
   [:p "Index accounts"]
   (if-not (seq accounts)
     [:div "No Accounts"]
     (into [:div.section]
           (for [account accounts]
             ^{:key (:db/id account)}
             (row-line account))))])
