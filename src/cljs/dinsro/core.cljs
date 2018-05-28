(ns dinsro.core
  (:require [ajax.core :refer [GET POST]]
            [dinsro.ajax :refer [load-interceptors!]]
            [dinsro.components :as c]
            [dinsro.components.navbar :refer [navbar]]
            [dinsro.state :refer [session]]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [reagent.core :as r]
            [re-material-ui-1.core :as ui]
            [secretary.core :as secretary :include-macros true])
  (:import goog.History))

;; create a new theme based on the dark theme from Material UI
(defonce theme-defaults
  {:theme
   (ui/create-mui-theme-fn (clj->js {:type "light"}))})

(defn home-page []
  (c/home-page (:docs @session)))

(defn users-page []
  (c/users-page session))

(def pages
  {:home #'home-page
   :users #'users-page
   :about #'c/about-page})

(defn page []
  [ui/mui-theme-provider theme-defaults
   [(pages (:page @session))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (swap! session assoc :page :home))

(secretary/defroute "/users" []
  (swap! session assoc :page :users))

(secretary/defroute "/about" []
  (swap! session assoc :page :about))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(swap! session assoc :docs %)}))

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
