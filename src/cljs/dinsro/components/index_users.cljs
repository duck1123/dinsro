(ns dinsro.components.index-users
  (:require
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.spec.users :as s.users]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(def default-error-message "")
(rf/reg-sub ::error-message (fn [db _] (get db ::error-message default-error-message)))

(defn user-link
  [user]
  (let [name (::s.users/name user)
        id (:db/id user)]
    [:a {:href (kf/path-for [:show-user-page {:id id}])} name]))

(defn user-line
  [user]
  (let [id (:db/id user)
        email (::s.users/email user)]
    [:tr
     [:td id]
     [:td [user-link user]]
     [:td email]
     (c.debug/hide [:td [c.buttons/delete-user user]])]))

(defn index-users
  [users]
  (if-not (seq users)
    [:div [:p (tr [:no-users])]]
    [:table.table
     [:thead
      [:tr
       [:th (tr [:id-label])]
       [:th (tr [:name-label])]
       [:th (tr [:email-label])]
       (c.debug/hide [:th "Buttons"])]]
     (into [:tbody]
           (for [{:keys [db/id] :as user} users]
             ^{:key id} [user-line user]))]))
