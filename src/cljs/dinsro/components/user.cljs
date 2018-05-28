(ns dinsro.components.user
  (:require [ajax.core :as ajax]
            [reagent.core :as r]))

(defn index-users
  [users]
  [:div
   ;; [list-user-accounts {:id id}]
   [:ul
    (for [user users]
      (let [id (get user "id")]
        [:li {:key user}
         [:a {:href (str "#/users/" id)}
          (get user "name")]]))]])

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
