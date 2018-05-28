(ns dinsro.components.navbar
  (:require [dinsro.state :refer [session]]))

(defn nav-link [uri title page]
  (c/nav-link (:page @session) uri title page))

(defn navbar []
  [:nav.navbar.navbar-dark.bg-primary.navbar-expand-md
   {:role "navigation"}
   [:button.navbar-toggler.hidden-sm-up
    {:type "button"
     :data-toggle "collapse"
     :data-target "#collapsing-navbar"}
    [:span.navbar-toggler-icon]]
   [:a.navbar-brand {:href "#/"} "dinsro"]
   [:div#collapsing-navbar.collapse.navbar-collapse
    [:ul.nav.navbar-nav.mr-auto
     [nav-link "#/" "Home" :home]
     [nav-link "#/users" "Users" :users]
     [nav-link "#/about" "About" :about]]]])
