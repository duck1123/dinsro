(ns dinsro.actions.home
  (:require [dinsro.layout :as layout]))

(defn home-handler
  [_]
  (layout/render "home.html"))
