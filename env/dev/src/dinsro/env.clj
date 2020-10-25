(ns dinsro.env
  (:require
   [dinsro.dev-middleware :refer [wrap-dev]]
   [selmer.parser :as parser]
   [taoensso.timbre :as timbre]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (timbre/info "-=[dinsro started successfully using the development profile]=-"))
   :stop
   (fn []
     (timbre/info "-=[dinsro has shut down successfully]=-"))
   :middleware wrap-dev})
