(ns dinsro.components.user-accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.components.forms.add-user-account :as c.f.add-user-account]
   [dinsro.components.links :as c.links]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn row-line
  [account]
  (let [id (:db/id account)
        initial-value (::s.accounts/initial-value account)
        currency-id (get-in account [::s.accounts/currency :db/id])]
    [:tr
     (c.debug/hide [:td id])
     [:td (c.links/account-link id)]
     [:td (c.links/currency-link currency-id)]
     [:td initial-value]
     (c.debug/hide [:td [c.buttons/delete-account account]])]))

(defn index-accounts
  [accounts]
  [:<>
   [c.debug/debug-box accounts]
   (if-not (seq accounts)
     [:div (tr [:no-accounts])]
     [:table.table
      [:thead
       [:tr
        (c.debug/hide [:th "Id"])
        [:th (tr [:name])]
        [:th (tr [:currency-label])]
        [:th (tr [:initial-value-label])]
        (c.debug/hide [:th (tr [:buttons])])]]
      (into [:tbody]
            (for [account accounts]
              ^{:key (:db/id account)}
              (row-line account)))])])

(defn section
  [user-id accounts]
  [:div.box
   [:h2
    (tr [:accounts])
    [c/show-form-button ::e.f.add-user-account/shown?]]
   [c.f.add-user-account/form user-id]
   [:hr]
   [index-accounts accounts]])

(s/fdef section
  :args (s/cat :user-id ::ds/id
               :accounts (s/coll-of ::s.accounts/item))
  :ret vector?)
