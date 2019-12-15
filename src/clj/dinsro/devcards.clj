(ns dinsro.devcards
  (:require [hiccup.page :refer [include-js include-css html5]]))

(def mount-target
  [:div#app
   [:h2 "Welcome to hello-devcard2"]
   [:p "please wait while Figwheel is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   #_(include-css (if true #_(env :dev) "/css/site.css" "/css/site.min.css"))])

(defn cards-page []
  (html5
   (head)
   [:body
    mount-target
    (include-js "/js/app_devcards.js")]))

(defn cards-handler
  [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (cards-page)})
