(ns dinsro.core
  (:require [dinsro.handler :as handler]
            [dinsro.nrepl :as nrepl]
            [luminus.http-server :as http]
            #_[luminus-migrations.core :as migrations]
            [dinsro.config :refer [env]]
            ;; [cider.nrepl :refer [cider-middleware]]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.logging :as log]
            [mount.core :as mount]
            [taoensso.timbre :as timbre])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (log/error {:what :uncaught-exception
                 :exception ex
                 :where (str "Uncaught exception on" (.getName thread))}))))

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
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (timbre/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (mount/start #'dinsro.config/env)
  (cond
    (nil? (:database-url env))
    (do
      (timbre/error "Database configuration not found, :database-url environment variable must be set before running")
      (System/exit 1))
    #_(some #{"init"} args)
    #_(do
      #_(migrations/init (select-keys env [:database-url :init-script]))
      (System/exit 0))
    #_(migrations/migration? args)
    #_(do
      #_(migrations/migrate args (select-keys env [:database-url]))
      (System/exit 0))
    :else
    (start-app args)))
