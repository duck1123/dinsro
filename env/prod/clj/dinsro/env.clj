(ns dinsro.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[dinsro started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[dinsro has shut down successfully]=-"))
   :middleware identity})
