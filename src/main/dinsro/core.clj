(ns dinsro.core
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [dinsro.config :refer [env]]
   [dinsro.handler :as handler]
   [dinsro.middleware]
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
    (-> env
        (assoc :handler (handler/app))
        (update :io-threads #(or % (* 2 (.availableProcessors (Runtime/getRuntime)))))
        (update :port #(or (-> env :options :port) %))))
  :stop
  (http/stop http-server))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (env :nrepl-port)
    (timbre/info "starting in core")
    (nrepl/start {:bind (env :nrepl-bind)
                  :handler (nrepl-handler)
                  :port (env :nrepl-port)}))
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

(defn -main [& args]
  (mount/start #'dinsro.config/env)
  (mount/start #'dinsro.config/secret)
  (mount/start #'dinsro.middleware/token-backend)
  (cond
    ;; (nil? (:datahike-url env))
    ;; (do
    ;;   (timbre/error "Database configuration not found, :database-url environment variable must be set before running")
    ;;   (System/exit 1))
    :else
    (start-app args)))
