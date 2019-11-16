(ns dinsro.actions.home
  (:require [dinsro.layout :as layout]))

(defn home-handler [request]
  (layout/render "home.html"))
