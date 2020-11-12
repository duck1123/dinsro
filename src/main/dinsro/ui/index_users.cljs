(ns dinsro.ui.index-users
  (:require
   [dinsro.specs.users :as s.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
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
     (u.debug/hide store [:td [u.buttons/delete-user store user]])]))

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
        (u.debug/hide store [:th "Buttons"])]]
      (into [:tbody]
            (for [{:keys [db/id] :as user} users]
              ^{:key id} [user-line store user]))]]))