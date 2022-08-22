(ns dinsro.components.nrepl
  (:require
   [dinsro.components.config :as config]
   [mount.core :as mount]
   [nrepl.server :as nrepl]
   [lambdaisland.glogc :as log]))

(defn nrepl-handler []
  (require 'cider.nrepl)
  (ns-resolve 'cider.nrepl 'cider-nrepl-handler))

(defn start
  "Start a network repl for debugging on specified port followed by
  an optional parameters map. The :bind, :transport-fn, :handler,
  :ack-port and :greeting-fn will be forwarded to
  clojure.tools.nrepl.server/start-server as they are."
  [{:keys [port bind transport-fn handler ack-port greeting-fn]}]
  (try
    (log/info :start/starting {:bind bind :port port})
    (nrepl/start-server :port port
                        :bind bind
                        :transport-fn transport-fn
                        :handler handler
                        :ack-port ack-port
                        :greeting-fn greeting-fn)

    (catch Throwable ex
      (log/error :start/failed {:ex ex})
      (throw ex))))

(defn stop [server]
  (nrepl/stop-server server)
  (log/info :stop/finished {}))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (config/config :nrepl-port)
    (let [bind (or (config/config :nrepl-bind) "0.0.0.0")
          port (config/config :nrepl-port)]
      (start {:bind    bind
              :handler (nrepl-handler)
              :port    port})))
  :stop
  (when repl-server
    (stop repl-server)))
