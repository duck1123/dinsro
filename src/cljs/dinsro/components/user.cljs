(ns dinsro.components.user
  (:require [ajax.core :as ajax]
            [dinsro.state :refer [session]]
            [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]))

(defn index-users
  [users]
  [:div])

(defn fetch-users
  [users-state]
  (ajax/GET "/api/v1/users"
            {:response-format :json
             :handler (fn [r] (reset! users-state r))}))

(defn users-page
  []
  (let [s @(subscribe [:users])]
    [:div
     [:h1 "Users Page"]
     [index-users s]]))

(defn show-user
  []
  (let [user-a (r/atom {})]
    (ajax/GET (str "/api/v1/users/" (:user-id @session))
      {:response-format :json
       :handler (fn [u] (reset! user-a u))})
    (fn []
      (let [{id "id"
             name "name"
             email "email"} @user-a]
        [:div
         [:h1 "Show Users"]
         [:p "Id: " id]
         [:p "name: " name]
         [:p "email: " email]]))))
