(ns dinsro.components.buttons
  (:require [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [dinsro.events.users :as e.users]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

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

(defn fetch-currencies
  []
  (let [state @(rf/subscribe [::e.currencies/do-fetch-index-state])]
    [:a.button {:on-click #(rf/dispatch [::e.currencies/do-fetch-index])}
     (tr [:fetch-currencies] [state])]))

(defn fetch-rates
  []
  (let [state @(rf/subscribe [::e.rates/do-fetch-index-state])]
    [:a.button {:on-click #(rf/dispatch [::e.rates/do-fetch-index])}
     (tr [:fetch-rates] [state])]))

(defn fetch-users
  []
  (let [state @(rf/subscribe [::e.users/do-fetch-index-state])]
    [:a.button {:on-click #(rf/dispatch [::e.users/do-fetch-index])}
     (tr [:fetch-users] [state])]))
