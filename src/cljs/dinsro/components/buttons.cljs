(ns dinsro.components.buttons
  (:require [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [dinsro.events.users :as e.users]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn fetch-accounts
  []
  (let [state @(rf/subscribe [::e.accounts/do-fetch-index-state])]
    [:a.button {:on-click #(rf/dispatch [::init-page])}
     (tr [:fetch-accounts "Fetch Accounts: %1"] [state])]))

(defn fetch-currencies
  []
  [:a.button {:on-click #(rf/dispatch [::e.currencies/do-fetch-index])}
   (str "Fetch Currencies: " @(rf/subscribe [::e.currencies/do-fetch-index-state]))])

(defn fetch-rates
  []
  (let [state @(rf/subscribe [::e.rates/do-fetch-index-state])]
    [:a.button {:on-click #(rf/dispatch [::e.rates/do-fetch-index])}
     (tr [:fetch-accounts "Fetch Rates: %1"] [state])]))

(defn fetch-users
  []
  [:a.button {:on-click #(rf/dispatch [::e.users/do-fetch-index])}
   (str "Fetch Users: " @(rf/subscribe [::e.users/do-fetch-index-state]))])
