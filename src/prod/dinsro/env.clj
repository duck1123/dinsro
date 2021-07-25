(ns dinsro.env
  (:require
   [taoensso.timbre :as log]))

(def defaults
  {:init
   (fn []
     (log/info "-=[dinsro started successfully]=-"))
   :stop
   (fn []
     (log/info "-=[dinsro has shut down successfully]=-"))
   :middleware identity})
