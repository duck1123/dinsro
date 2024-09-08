(ns dinsro.components.server
  (:require
   [dinsro.components.config :as c.config]
   [dinsro.components.ring-middleware :as c.ring-middleware]
   [lambdaisland.glogc :as log]
   [mount.core :refer [defstate]]
   [org.httpkit.server :refer [run-server]]))

(declare http-server)

(defn start-http-server!
  []
  (log/info :start-http-server!/starting {})
  (let [config (c.config/get-config)
        cfg     (get config :org.httpkit.server/config)
        stop-fn (run-server @c.ring-middleware/middleware cfg)]
    (log/info :start-http-server!/finished {:cfg cfg})
    {:stop stop-fn}))

(defn stop-http-server!
  []
  (log/info :stop-http-server!/starting {})
  (let [{:keys [stop]} @http-server]
    (when stop
      (stop))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defstate
  ^{:doc "The main web server for the application"}
  http-server
  :start (start-http-server!)
  :stop (stop-http-server!))
