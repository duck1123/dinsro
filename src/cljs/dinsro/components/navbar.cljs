(ns dinsro.components.navbar
  (:require [dinsro.state :refer [session]]
            [re-material-ui-1.core :as ui]))

(defn nav-link [uri title page]
  [:li.nav-item
   {:class (when (= page (:page @session)) "active")}
   [:a.nav-link {:href uri} title]])

(defn navbar []
  (fn []
    [ui/app-bar {:position "static" :color "default"}
     [ui/toolbar
      [ui/icon-button {:color "inherit" :aria-label "Menu"}
       [ui/menu-icon]]
      [ui/typography {:variant "title" :color "inherit"}
       "Dinsro"]
      [ui/button {:color "inherit"} "Login"]]]))
