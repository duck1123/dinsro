(ns dinsro.ui.buttons
  (:require
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.rates :as e.rates]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(defn delete-account
  [store account]
  [:a.button.is-danger
   {:on-click #(st/dispatch store [::e.accounts/do-delete-record account])}
   (tr [:delete])])

(defn delete-category
  [store category]
  [:a.button.is-danger
   {:on-click #(st/dispatch store [::e.categories/do-delete-record category])}
   (tr [:delete])])

(defn delete-currency
  [store currency]
  [:a.button.is-danger
   {:on-click #(st/dispatch store [::e.currencies/do-delete-record currency])}
   (tr [:delete])])

(defn delete-rate
  [store item]
  [:a.button.is-danger
   {:on-click #(st/dispatch store [::e.rates/do-delete-record item])}
   (tr [:delete])])

(defn delete-rate-source
  [store item]
  [:a.button.is-danger
   {:on-click #(st/dispatch store [::e.rate-sources/do-delete-record item])}
   (tr [:delete])])

(defn delete-transaction
  [store item]
  [:a.button.is-danger
   {:on-click #(st/dispatch store [::e.transactions/do-delete-record item])}
   (tr [:delete])])

(defn delete-user
  [store user]
  [:a.button.is-danger
   {:on-click #(st/dispatch store [::e.users/do-delete-record user])}
   (tr [:delete])])

(defn fetch-accounts
  [store]
  (let [state @(st/subscribe store [::e.accounts/do-fetch-index-state])]
    [:a.button {:on-click #(st/dispatch store [::e.accounts/do-fetch-index])}
     (tr [:fetch-accounts] [state])]))

(defn fetch-categories
  [store]
  (let [state @(st/subscribe store [::e.categories/do-fetch-index-state])]
    [:a.button {:on-click #(st/dispatch store [::e.categories/do-fetch-index])}
     (tr [:fetch-categories] [state])]))

(defn fetch-currencies
  [store]
  (let [state @(st/subscribe store [::e.currencies/do-fetch-index-state])]
    [:a.button {:on-click #(st/dispatch store [::e.currencies/do-fetch-index])}
     (tr [:fetch-currencies] [state])]))

(defn fetch-currency
  [store currency-id]
  (let [state @(st/subscribe store [::e.currencies/do-fetch-record-state])]
    [:a.button {:on-click #(st/dispatch store [::e.currencies/do-fetch-record currency-id])}
     (tr [:fetch-currency] [currency-id state])]))

(defn fetch-rate-sources
  [store]
  (let [state @(st/subscribe store [::e.rate-sources/do-fetch-index-state])]
    [:a.button {:on-click #(st/dispatch store [::e.rate-sources/do-fetch-index])}
     (tr [:fetch-rate-sources] [state])]))

(defn fetch-rates
  [store]
  (let [state @(st/subscribe store [::e.rates/do-fetch-index-state])]
    [:a.button {:on-click #(st/dispatch store [::e.rates/do-fetch-index])}
     (tr [:fetch-rates] [state])]))

(defn fetch-transactions
  [store]
  (let [state @(st/subscribe store [::e.transactions/do-fetch-index-state])]
    [:a.button {:on-click #(st/dispatch store [::e.transactions/do-fetch-index])}
     (tr [:fetch-transactions] [state])]))

(defn fetch-user
  [store id]
  (let [state @(st/subscribe store [::e.users/do-fetch-record-state])]
    [:button.button {:on-click #(st/dispatch store [::e.users/do-fetch-record id])}
     (str "Load User: " state " -> " id)]))

(defn fetch-users
  [store]
  (let [state @(st/subscribe store [::e.users/do-fetch-index-state])]
    [:a.button {:on-click #(st/dispatch store [::e.users/do-fetch-index])}
     (tr [:fetch-users] [state])]))

(defn close-button
  [store key]
  [:a.delete.is-pulled-right
   {:on-click #(st/dispatch store [key false])}])

(defn show-form-button
  ([store state]
   (show-form-button store state (#'rfu/kw-prefix state "set-")))
  ([store state change]
   (when-not @(st/subscribe store [state])
     [:a.is-pulled-right {:on-click #(st/dispatch store [change true])}
      (tr [:show-form "Show"])])))
