(ns dinsro.components.admin-index-accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.forms.admin-create-account :as c.f.admin-create-account]
   [dinsro.components.links :as c.links]
   [dinsro.events.admin-accounts :as e.admin-accounts]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn row-line
  [store account]
  (let [id (:db/id account)
        initial-value (::s.accounts/initial-value account)
        currency-id (get-in account [::s.accounts/currency :db/id])
        user-id (get-in account [::s.accounts/user :db/id])]
    [:tr
     (c.debug/hide store [:td id])
     [:td (c.links/account-link store id)]
     [:td (c.links/user-link store user-id)]
     [:td (c.links/currency-link store currency-id)]
     [:td initial-value]
     (c.debug/hide store [:td [c.buttons/delete-account account]])]))

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
       (c.debug/hide store [:th "Id"])
       [:th (tr [:name])]
       [:th (tr [:user-label])]
       [:th (tr [:currency-label])]
       [:th (tr [:initial-value-label])]
       (c.debug/hide store [:th (tr [:buttons])])]]
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
      [c/show-form-button store ::e.f.create-account/shown?]]
     [c.f.admin-create-account/form store]
     [:hr]
     (condp = state
       :invalid [:p "Invalid"]
       :loaded  (when accounts [index-accounts store accounts])
       [:p "Unknown state: " state])]))
