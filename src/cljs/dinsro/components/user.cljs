(ns dinsro.components.user
  (:require [ajax.core :as ajax]
            [dinsro.state :refer [session]]
            [reagent.core :as r]
            [re-material-ui-1.core :as ui]))

(defn index-users
  [users]
  [:div
   ;; [list-user-accounts {:id id}]
   [ui/menu-list
    (for [user users]
      (let [id (get user "id")]
        [ui/list-item {:key id
                       :button true
                       :component "a"
                       :href (str "#/users/" id)}
         [ui/list-item-text
          [:span
           (get user "name")]]]))]])

(defn fetch-users
  [users-state]
  (ajax/GET "/api/users"
    {:response-format :json
     :handler (fn [r] (reset! users-state r))}))

(defn users-page
  []
  (let [users-state (r/atom [])]
    (fetch-users users-state)
    (fn []
      [:div
       [:h1 "Users"]
       [index-users @users-state]])))

(defn show-user
  []
  [:div
   [:h1 "Show Users"]
   [:p "ID: " (:user-id @session)]])
