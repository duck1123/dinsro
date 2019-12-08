(ns dinsro.components.index-accounts
  (:require [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.users :as s.users]
            [dinsro.translations :refer [tr]]
            [dinsro.views.show-account :as v.show-account]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn delete-button
  [id]
  [:a.button.is-danger
   {:on-click #(rf/dispatch [::e.accounts/do-delete-account id])}
   (tr [:delete])])

(defn account-link
  [account]
  (let [id (:db/id account)
        name (::s.accounts/name account)]
    [:a {:href (kf/path-for [:show-account-page {:id id}])} name]))

(defn currency-link
  [currency-id]
  (if-let [currency @(rf/subscribe [::e.currencies/item currency-id])]
    (let [name (::s.currencies/name currency)]
      [:a {:href (kf/path-for [:show-currency-page {:id currency-id}])} name])
    [:span (tr [:not-loaded])]))

(defn user-link
  [user-id]
  (if-let [user @(rf/subscribe [::e.users/item user-id])]
    (let [name (::s.users/name user)]
      [:a {:href (kf/path-for [:show-user-page {:id user-id}])} name])
    [:span (tr [:not-loaded])]))

(defn row-line
  [account]
  (let [{:keys [db/id
                dinsro.spec.accounts/currency
                dinsro.spec.accounts/name
                dinsro.spec.accounts/user]} account
        initial-value (::s.accounts/initial-value account)
        currency-id (:db/id currency)
        user-id (:db/id user)]
    [:div.box
     [:p [account-link account]]
     [:p (tr [:user-label] [[user-link user-id]])]
     [:p (tr [:currency-label] [[currency-link]])]
     [:p (tr [:initial-value-label] [initial-value])]
     [delete-button id]]))

(defn index-accounts
  [accounts]
  [:div.box
   (if-not (seq accounts)
     [:div (tr [:no-accounts])]
     (into [:div]
           (for [account accounts]
             ^{:key (:db/id account)}
             (row-line account))))])
