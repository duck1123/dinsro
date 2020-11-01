(ns dinsro.components.user-accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.forms.add-user-account :as c.f.add-user-account]
   [dinsro.components.links :as c.links]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.specs :as ds]
   [dinsro.specs.accounts :as s.accounts]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn row-line
  [store account]
  (let [id (:db/id account)
        initial-value (::s.accounts/initial-value account)
        currency-id (get-in account [::s.accounts/currency :db/id])]
    [:tr
     (c.debug/hide store [:td id])
     [:td (c.links/account-link store id)]
     [:td (c.links/currency-link store currency-id)]
     [:td initial-value]
     (c.debug/hide store [:td [c.buttons/delete-account store account]])]))

(defn index-accounts
  [store accounts]
  (if-not (seq accounts)
    [:div (tr [:no-accounts])]
    [:table.table
     [:thead
      [:tr
       (c.debug/hide store [:th "Id"])
       [:th (tr [:name])]
       [:th (tr [:currency-label])]
       [:th (tr [:initial-value-label])]
       (c.debug/hide store [:th (tr [:buttons])])]]
     (into [:tbody]
           (for [account accounts]
             ^{:key (:db/id account)}
             (row-line store account)))]))

(defn section
  [store user-id accounts]
  [:div.box
   [:h2
    (tr [:accounts])
    [c/show-form-button store ::e.f.add-user-account/shown?]]
   [c.f.add-user-account/form store user-id]
   [:hr]
   [index-accounts store accounts]])

(s/fdef section
  :args (s/cat :user-id ::ds/id
               :accounts (s/coll-of ::s.accounts/item))
  :ret vector?)
