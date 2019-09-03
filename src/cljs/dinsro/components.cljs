(ns dinsro.components
  (:require [ajax.core :as ajax]
            [reagent.core :as r]
            [re-material-ui-1.core :as ui]))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src "/img/warning_clojure.png"}]]]])

(defn home-page []
  [:div.container
   [:h1 "Home Page"]])
