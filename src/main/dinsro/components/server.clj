(ns dinsro.components.server
  (:require
   [dinsro.components.config :refer [config]]
   [dinsro.components.ring-middleware :refer [middleware]]
   [lambdaisland.glogc :as log]
   [mount.core :refer [defstate]]
   [org.httpkit.server :refer [run-server]]))

(defstate http-server
  "The main web server for the application"
  :start
  (let [cfg     (get config :org.httpkit.server/config)
        stop-fn (run-server middleware cfg)]
    (log/info :http-server/starting {:cfg cfg})
    {:stop stop-fn})
  :stop
  (let [{:keys [stop]} http-server]
    (when stop
      (stop))))
