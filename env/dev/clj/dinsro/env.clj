(ns dinsro.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [dinsro.dev-middleware :refer [wrap-dev]]
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
