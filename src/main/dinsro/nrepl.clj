(ns dinsro.nrepl
  (:require
   [nrepl.server :as nrepl]
   [taoensso.timbre :as timbre]))

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
