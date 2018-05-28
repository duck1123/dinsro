(ns dinsro.components
  (:require [ajax.core :as ajax]
            [dinsro.state :refer [session]]
            [markdown.core :refer [md->html]]
            [reagent.core :as r]))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src "/img/warning_clojure.png"}]]]])

(defn home-page []
  [:div.container
   (when-let [docs (:docs @session)]
     [:div.row>div.col-sm-12
      [:div {:dangerouslySetInnerHTML
             {:__html (md->html docs)}}]])])
