(ns dinsro.components.user
  (:require [ajax.core :as ajax]
            [reagent.core :as r]))

(defn index-users
  [users]
  [:div
   ;; [list-user-accounts {:id id}]
   [:ul
    (for [user users]
      ^{:key user}
      [:li
       [:p (get user "id") " - " (get user "name")]])]])

(defn users-page
  []
  (let [users (r/atom [])]
    (ajax/GET "/api/users"
      {:response-format :json
       :handler (fn [r] (reset! users r))})
    (fn []
      [:div
       [:h1 "Users"]
       [index-users @users]])))
