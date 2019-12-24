(ns dinsro.env
  (:require [clojure.tools.logging :as log]
            [taoensso.timbre :as timbre]))

(def defaults
  {:init
   (fn []
     (timbre/info "-=[dinsro started successfully]=-"))
   :stop
   (fn []
     (timbre/info "-=[dinsro has shut down successfully]=-"))
   :middleware identity})
