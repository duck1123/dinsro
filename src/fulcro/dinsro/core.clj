(ns dinsro.core
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [dinsro.config]
   [dinsro.components.config :as config]
   [dinsro.handler :as handler]
   [dinsro.middleware.middleware]
   [dinsro.nrepl :as nrepl]
   [luminus.http-server :as http]
   [mount.core :as mount]
   [taoensso.timbre :as timbre])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (timbre/error {:what :uncaught-exception
                    :exception ex
                    :where (str "Uncaught exception on" (.getName thread))} ex))))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(defn nrepl-handler []
  (require 'cider.nrepl)
  (ns-resolve 'cider.nrepl 'cider-nrepl-handler))

(mount/defstate ^{:on-reload :noop} http-server
  :start
  (http/start
   (-> config/config
       (assoc :handler (handler/app))
       (update :io-threads #(or % (* 2 (.availableProcessors (Runtime/getRuntime)))))
       (update :port #(or (-> config/config :options :port) %))))
  :stop
  (http/stop http-server))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (config/config :nrepl-port)
    (timbre/info "starting in core")
    (nrepl/start {:bind (config/config :nrepl-bind)
                  :handler (nrepl-handler)
                  :port (config/config :nrepl-port)}))
  :stop
  (when repl-server
    (nrepl/stop repl-server)))

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (timbre/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (timbre/info "starting app")
  (let [options (parse-opts args cli-options)]
    (doseq [component (-> options mount/start-with-args :started)]
      (timbre/info component "started")))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main
  [& args]
  (mount/start #'dinsro.components.config/config)
  (mount/start #'dinsro.config/secret)
  (mount/start #'dinsro.middleware.middleware/token-backend)
  (cond
    (nil? (:datahike-url config/config))
    (do
      (timbre/error "Database configuration not found, :datahike-url environment variable must be set before running")
      (System/exit 1))
    :else
    (start-app args)))
