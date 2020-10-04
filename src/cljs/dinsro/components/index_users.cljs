(ns dinsro.components.index-users
  (:require
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.spec.users :as s.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(def default-error-message "")
(rf/reg-sub ::error-message (fn [db _] (get db ::error-message default-error-message)))

(defn user-link
  [store user]
  (let [name (::s.users/name user)
        id (:db/id user)]
    [:a {:href (st/path-for store [:show-user-page {:id id}])} name]))

(defn user-line
  [store user]
  (let [id (:db/id user)
        email (::s.users/email user)]
    [:tr
     [:td id]
     [:td [user-link store user]]
     [:td email]
     (c.debug/hide store [:td [c.buttons/delete-user store user]])]))

(defn index-users
  [store users]
  (if-not (seq users)
    [:div [:p (tr [:no-users])]]
    [:div
     [:p [:a {:href (kf/path-for [:admin-index-users-page])} "Users"]]
     [:table.table
      [:thead
       [:tr
        [:th (tr [:id-label])]
        [:th (tr [:name-label])]
        [:th (tr [:email-label])]
        (c.debug/hide store [:th "Buttons"])]]
      (into [:tbody]
            (for [{:keys [db/id] :as user} users]
              ^{:key id} [user-line store user]))]]))
