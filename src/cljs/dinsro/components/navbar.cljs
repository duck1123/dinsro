(ns dinsro.components.navbar
  (:require [dinsro.state :refer [session]]
            [re-material-ui-1.core :as ui]))

(defn nav-link [uri title page]
  [ui/list-item {:button true
                 :component "a"
                 :href uri}
   [ui/list-item-text [:div title]]])

(defn navbar []
  (fn []
    [:div
     [ui/app-bar {:position "static" :color "default"}
      [ui/toolbar
       [ui/icon-button {:color "inherit" :aria-label "Menu"}
        [ui/menu-icon]]
       [ui/typography {:variant "title" :color "inherit"}
        "Dinsro"]
       [ui/button {:color "inherit"} "Login"]]]
     [ui/menu-list
      [nav-link "#/" "Home" :home]
      [nav-link "#/users" "Users" :users]
      [nav-link "#/register" "Register" :register]
      [nav-link "#/about" "About" :about]]]))
