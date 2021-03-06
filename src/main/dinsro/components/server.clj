(ns dinsro.components.server
  (:require
   [org.httpkit.server :refer [run-server]]
   [mount.core :refer [defstate]]
   [taoensso.timbre :as log]
   [dinsro.components.config :refer [config]]
   [dinsro.components.ring-middleware :refer [middleware]]))

(defstate http-server
  :start
  (let [cfg     (get config :org.httpkit.server/config)
        stop-fn (run-server middleware cfg)]
    (log/info "Starting webserver with config " cfg)
    {:stop stop-fn})
  :stop
  (let [{:keys [stop]} http-server]
    (when stop
      (stop))))
