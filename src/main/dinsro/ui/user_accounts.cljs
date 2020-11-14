(ns dinsro.ui.user-accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.add-user-account :as u.f.add-user-account]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(defn row-line
  [store account]
  (let [id (:db/id account)
        initial-value (::m.accounts/initial-value account)
        currency-id (get-in account [::m.accounts/currency :db/id])]
    [:tr
     (u.debug/hide store [:td id])
     [:td (u.links/account-link store id)]
     [:td (u.links/currency-link store currency-id)]
     [:td initial-value]
     (u.debug/hide store [:td [u.buttons/delete-account store account]])]))

(defn index-accounts
  [store accounts]
  (if-not (seq accounts)
    [:div (tr [:no-accounts])]
    [:table.table
     [:thead
      [:tr
       (u.debug/hide store [:th "Id"])
       [:th (tr [:name])]
       [:th (tr [:currency-label])]
       [:th (tr [:initial-value-label])]
       (u.debug/hide store [:th (tr [:buttons])])]]
     (into [:tbody]
           (for [account accounts]
             ^{:key (:db/id account)}
             (row-line store account)))]))

(defn section
  [store user-id accounts]
  [:div.box
   [:h2
    (tr [:accounts])
    [u/show-form-button store ::e.f.add-user-account/shown?]]
   [u.f.add-user-account/form store user-id]
   [:hr]
   [index-accounts store accounts]])

(s/fdef section
  :args (s/cat :user-id ::ds/id
               :accounts (s/coll-of ::m.accounts/item))
  :ret vector?)
