(ns dinsro.actions.home
  (:require [dinsro.layout :as layout]))

(defn home-page [request]
  (layout/render "home.html"))
