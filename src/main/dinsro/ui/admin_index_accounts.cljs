(ns dinsro.ui.admin-index-accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.admin-accounts :as e.admin-accounts]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.specs.accounts :as s.accounts]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.admin-create-account :as u.f.admin-create-account]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(defn row-line
  [store account]
  (let [id (:db/id account)
        initial-value (::s.accounts/initial-value account)
        currency-id (get-in account [::s.accounts/currency :db/id])
        user-id (get-in account [::s.accounts/user :db/id])]
    [:tr
     (u.debug/hide store [:td id])
     [:td (u.links/account-link store id)]
     [:td (u.links/user-link store user-id)]
     [:td (u.links/currency-link store currency-id)]
     [:td initial-value]
     (u.debug/hide store [:td [u.buttons/delete-account store account]])]))

(s/fdef row-line
  :args (s/cat :account ::s.accounts/item)
  :ret vector?)

(defn index-accounts
  [store accounts]
  (if-not (seq accounts)
    [:div (tr [:no-accounts])]
    [:table.table
     [:thead
      [:tr
       (u.debug/hide store [:th "Id"])
       [:th (tr [:name])]
       [:th (tr [:user-label])]
       [:th (tr [:currency-label])]
       [:th (tr [:initial-value-label])]
       (u.debug/hide store [:th (tr [:buttons])])]]
     (into [:tbody]
           (for [account accounts]
             ^{:key (:db/id account)}
             (row-line store account)))]))

(s/fdef index-accounts
  :args (s/cat :accounts (s/coll-of ::s.accounts/item))
  :ret vector?)

(defn section
  [store]
  (let [accounts @(st/subscribe store [::e.admin-accounts/items])
        state @(st/subscribe store [::e.admin-accounts/do-fetch-index-state])]
    [:div.box
     [:h1
      (tr [:index-accounts])
      [u/show-form-button store ::e.f.create-account/shown?]]
     [u.f.admin-create-account/form store]
     [:hr]
     (condp = state
       :invalid [:p "Invalid Fetch state"]
       :loaded  (when accounts [index-accounts store accounts])
       [:p "Unknown state: " state])]))
