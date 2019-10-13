(ns dinsro.components.user
  (:require [ajax.core :as ajax]
            [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]))

(defn show-user
  []
  (let [user-a (r/atom {})
        {id "id" name "name" email "email"} @user-a]
    [:div
     [:h1 "Show Users"]
     [:p "Id: " id]
     [:p "name: " name]
     [:p "email: " email]]))

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Show User"]])
