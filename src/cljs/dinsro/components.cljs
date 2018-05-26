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

(defn nav-link [current-page uri title page]
  [:li.nav-item
   {:class (when (= page current-page) "active")}
   [:a.nav-link {:href uri} title]])
