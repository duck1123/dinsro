(ns dinsro.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [dinsro.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[dinsro started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[dinsro has shut down successfully]=-"))
   :middleware wrap-dev})
