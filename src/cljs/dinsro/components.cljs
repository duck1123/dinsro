(ns dinsro.components
  (:require [markdown.core :refer [md->html]]))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src "/img/warning_clojure.png"}]]]])

(defn home-page [docs]
  [:div.container
   (when docs
     [:div.row>div.col-sm-12
      [:div {:dangerouslySetInnerHTML
             {:__html (md->html docs)}}]])])

(defn index-users
  [users]
  [:div
   ;; [list-user-accounts {:id id}]
   (map
    (fn [user]
      [:ul
       [:li
        [:p (:id user) " - " (:name user)]]])
    users)])

(defn users-page
  [session]
  [:div
   [:h1 "Users"]
   (let [users []]
     [index-users users])])
