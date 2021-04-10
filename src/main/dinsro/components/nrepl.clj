(ns dinsro.components.nrepl
  (:require
   [dinsro.components.config :as config]
   [dinsro.nrepl :as nrepl]
   [mount.core :as mount]
   [taoensso.timbre :as timbre]))

(defn nrepl-handler []
  (require 'cider.nrepl)
  (ns-resolve 'cider.nrepl 'cider-nrepl-handler))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (config/config :nrepl-port)
    (timbre/info "starting in core")
    (nrepl/start {:bind    (or (config/config :nrepl-bind) "0.0.0.0")
                  :handler (nrepl-handler)
                  :port    (config/config :nrepl-port)}))
  :stop
  (when repl-server
    (nrepl/stop repl-server)))
