(ns dinsro.core
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [dinsro.components.config :as config]
   [dinsro.components.http]
   [dinsro.components.nrepl]
   [dinsro.components.secrets :as secrets]
   [dinsro.middleware :as middleware]
   [mount.core :as mount]
   [taoensso.timbre :as log])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (log/error {:what      :uncaught-exception
                 :exception ex
                 :where     (str "Uncaught exception on" (.getName thread))} ex))))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (log/info "starting app")
  (let [options (parse-opts args cli-options)]
    (doseq [component (-> options mount/start-with-args :started)]
      (log/debug component "started")))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main
  [& args]
  (let [[config-file] args]
    (-> (mount/only #{#'config/config})
        (mount/with-args {:config config-file})
        mount/start))
  (mount/start #'secrets/secret)
  (mount/start #'middleware/token-backend)
  (start-app args))
