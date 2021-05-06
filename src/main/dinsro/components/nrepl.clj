(ns dinsro.components.nrepl
  (:require
   [dinsro.components.config :as config]
   [mount.core :as mount]
   [nrepl.server :as nrepl]
   [taoensso.timbre :as timbre]))

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
    (timbre/info "starting nREPL server on port" port)
    (nrepl/start-server :port port
                        :bind bind
                        :transport-fn transport-fn
                        :handler handler
                        :ack-port ack-port
                        :greeting-fn greeting-fn)

    (catch Throwable t
      (timbre/error t "failed to start nREPL")
      (throw t))))

(defn stop [server]
  (nrepl/stop-server server)
  (timbre/info "nREPL server stopped"))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (config/config :nrepl-port)
    (timbre/info "starting in core")
    (start {:bind    (or (config/config :nrepl-bind) "0.0.0.0")
            :handler (nrepl-handler)
            :port    (config/config :nrepl-port)}))
  :stop
  (when repl-server
    (stop repl-server)))
