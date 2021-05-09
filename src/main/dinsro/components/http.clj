(ns dinsro.components.http
  (:require
   [dinsro.components.config :as config]
   [dinsro.handler :as handler]
   [luminus.http-server :as http]
   [mount.core :as mount]
   [taoensso.timbre :as log]))

(mount/defstate ^{:on-reload :noop} http-server
  :start
  (http/start
   (-> config/config
       (assoc :handler (handler/app))
       (update :io-threads #(or % (* 2 (.availableProcessors (Runtime/getRuntime)))))
       (update :port #(or (-> config/config :options :port) %))))
  :stop
  (http/stop http-server))
