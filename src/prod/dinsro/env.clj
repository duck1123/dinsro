(ns dinsro.env
  (:require
   [lambdaisland.glogc :as log]))

(def defaults
  {:init
   (fn []
     (log/info :defaults/starting {}))
   :stop
   (fn []
     (log/info :defaults/stopping {}))
   :middleware identity})
