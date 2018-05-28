(ns dinsro.components
  (:require [dinsro.components.user :as user]
            [markdown.core :refer [md->html]]))

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

(defn users-page
  [session]
  [:div
   [:h1 "Users"]
   (let [users []]
     [user/index-users users])])
