(ns dinsro.components.nrepl
  (:require
   [dinsro.components.config :as config]
   [lambdaisland.glogc :as log]
   [mount.core :as mount]
   [nrepl.server :as nrepl]))

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

(declare repl-server)

(defn start-repl-server!
  []
  (log/info :repl-server/starting {})
  (let [config                                         (config/get-config)
        {:keys [nrepl-port nrepl-bind]
         :or   {nrepl-port 7000 nrepl-bind "0.0.0.0"}} config]
    (start {:bind    nrepl-bind
            :handler (nrepl-handler)
            :port    nrepl-port})))

(defn stop-repl-server!
  []
  (let [server @repl-server]
    (log/info :repl-serving/stopping {:server server})
    (when server (stop server))))

(mount/defstate ^{:on-reload :noop} repl-server
  :start (start-repl-server!)
  :stop (stop-repl-server!))
