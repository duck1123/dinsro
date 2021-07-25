(ns dinsro.env
  (:require
   [dinsro.dev-middleware :refer [wrap-dev]]
   [selmer.parser :as parser]
   [taoensso.timbre :as log]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "-=[dinsro started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "-=[dinsro has shut down successfully]=-"))
   :middleware wrap-dev})
