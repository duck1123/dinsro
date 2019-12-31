(ns dinsro.components.buttons
  (:require [dinsro.events.accounts :as e.accounts]
            [dinsro.events.categories :as e.categories]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.events.users :as e.users]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn delete-account
  [account]
  [:a.button.is-danger
   {:on-click #(rf/dispatch [::e.accounts/do-delete-record account])}
   (tr [:delete])])

(defn delete-category
  [category]
  [:a.button.is-danger
   {:on-click #(rf/dispatch [::e.categories/do-delete-record category])}
   (tr [:delete])])

(defn delete-currency
  [currency]
  [:a.button.is-danger
   {:on-click #(rf/dispatch [::e.currencies/do-delete-record currency])}
   (tr [:delete])])

(defn delete-rate
  [item]
  [:a.button.is-danger
   {:on-click #(rf/dispatch [::e.rates/do-delete-record item])}
   (tr [:delete])])

(defn delete-user
  [user]
  [:a.button.is-danger
   {:on-click #(rf/dispatch [::e.users/do-delete-record user])}
   (tr [:delete])])

(defn fetch-accounts
  []
  (let [state @(rf/subscribe [::e.accounts/do-fetch-index-state])]
    [:a.button {:on-click #(rf/dispatch [::e.accounts/do-fetch-index])}
     (tr [:fetch-accounts] [state])]))

(defn fetch-categories
  []
  (let [state @(rf/subscribe [::e.categories/do-fetch-index-state])]
    [:a.button {:on-click #(rf/dispatch [::e.categories/do-fetch-index])}
     (tr [:fetch-categories] [state])]))

(defn fetch-currencies
  []
  (let [state @(rf/subscribe [::e.currencies/do-fetch-index-state])]
    [:a.button {:on-click #(rf/dispatch [::e.currencies/do-fetch-index])}
     (tr [:fetch-currencies] [state])]))

(defn fetch-currency
  [currency-id]
  (let [state @(rf/subscribe [::e.currencies/do-fetch-record-state])]
    [:a.button {:on-click #(rf/dispatch [::e.currencies/do-fetch-record currency-id])}
     (tr [:fetch-currency] [currency-id state])]))

(defn fetch-rates
  []
  (let [state @(rf/subscribe [::e.rates/do-fetch-index-state])]
    [:a.button {:on-click #(rf/dispatch [::e.rates/do-fetch-index])}
     (tr [:fetch-rates] [state])]))

(defn fetch-transactions
  []
  (let [state @(rf/subscribe [::e.transactions/do-fetch-index-state])]
    [:a.button {:on-click #(rf/dispatch [::e.transactions/do-fetch-index])}
     (tr [:fetch-transactions] [state])]))

(defn fetch-user
  [id]
  (let [state @(rf/subscribe [::e.users/do-fetch-record-state])]
    [:button.button {:on-click #(rf/dispatch [::e.users/do-fetch-record id])}
     (str "Load User: " state " -> " id)]))

(defn fetch-users
  []
  (let [state @(rf/subscribe [::e.users/do-fetch-index-state])]
    [:a.button {:on-click #(rf/dispatch [::e.users/do-fetch-index])}
     (tr [:fetch-users] [state])]))
